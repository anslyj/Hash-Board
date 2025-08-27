package gui;

import datastructures.*;
import datastructures.NotImplemented;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** Interactive window for experimenting with different hashing schemes. */
public class HashGUI extends JFrame {

    /* ------------------------------------------------------------------ */
    /*  Controls                                                          */
    /* ------------------------------------------------------------------ */

    private final JComboBox<String> styleBox = new JComboBox<>(new String[]{
            "Separate Chaining", "Linear", "Quadratic", "Double Hash"});

    private final JComboBox<String> hashBox = new JComboBox<>(
            new String[]{
                    "Division",        // 0
                    "Multiplication",  // 1
                    "Mid-Square",      // 2
                    "Folding",         // 3
                    "Fibonacci",       // 4
                    "Random",           // 5
                    "Custom Mix",       // 6
                    "Some Other Hash Function"    // 7
            });

    /** table-size slider (5 – 201, step 2) */
    private final JSlider sizeSlider = new JSlider(5, 201, 10);

    /** operations count (inserts + deletes) */
    private final JSpinner opsSpin = new JSpinner(new SpinnerNumberModel(100, 10, 10_000, 10));

    /** 0 = all deletes, 100 = all inserts */
    private final JSlider ratioSlider = new JSlider(0, 100, 100);

    private final JComboBox<String> dataBox = new JComboBox<>(
            new String[]{"Uniform", "Left-skewed", "Right-skewed", "Clustered", "Bimodal"});

    /** key-range spinners */
    private final JSpinner minKeySpin = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner maxKeySpin = new JSpinner(new SpinnerNumberModel(100, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

    private final JTextArea stats = new JTextArea(10, 32);
    /** one‑line run summary (collisions, insertions, rate) */
    private final JTextField summaryField = new JTextField(35);

    /* ------------------------------------------------------------------ */

    private HashTable table;
    private final Board board = new Board();

    public HashGUI() {
        super("Hash Table Visualization");
        buildUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /* ================================================================== */
    /*  Build UI                                                          */
    /* ================================================================== */

    private void buildUI() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel sizeLbl  = new JLabel("Table size: 10");
        JLabel ratioLbl = new JLabel("Insert % : 100");

        sizeSlider.setMajorTickSpacing(50);
        sizeSlider.setMinorTickSpacing(5);
        sizeSlider.addChangeListener(e -> sizeLbl.setText("Table size: " + sizeSlider.getValue()));
        ratioSlider.addChangeListener(e -> ratioLbl.setText("Insert % : " + ratioSlider.getValue()));

        left.add(sizeLbl);                left.add(sizeSlider);         left.add(Box.createVerticalStrut(8));
        left.add(new JLabel("Total ops (N)")); left.add(opsSpin);       left.add(Box.createVerticalStrut(8));
        left.add(ratioLbl);               left.add(ratioSlider);        left.add(Box.createVerticalStrut(8));

        left.add(new JLabel("Hash function")); left.add(hashBox);       left.add(Box.createVerticalStrut(8));
        left.add(new JLabel("Table style"));   left.add(styleBox);      left.add(Box.createVerticalStrut(8));
        left.add(new JLabel("Data pattern"));  left.add(dataBox);       left.add(Box.createVerticalStrut(8));

        left.add(new JLabel("Key range  [min … max]"));
        JPanel rangeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        rangeRow.add(minKeySpin); rangeRow.add(new JLabel("…")); rangeRow.add(maxKeySpin);
        left.add(rangeRow);               left.add(Box.createVerticalStrut(12));

        JButton runBtn   = new JButton("Run");
        JButton resetBtn = new JButton("Reset");
        runBtn.addActionListener(this::handleRun);
        resetBtn.addActionListener(e -> { table = null; board.setModel(null); stats.setText(""); });

        left.add(runBtn);         left.add(Box.createVerticalStrut(4)); left.add(resetBtn);
        left.add(Box.createVerticalStrut(12));

        summaryField.setEditable(false);
        summaryField.setBorder(BorderFactory.createTitledBorder("Run Summary"));
        left.add(summaryField);
        left.add(Box.createVerticalStrut(8));

        stats.setEditable(false);
        stats.setBackground(new Color(245, 245, 245));
        left.add(new JScrollPane(stats));

        add(left,  BorderLayout.WEST);
        add(board, BorderLayout.CENTER);
    }

    /* ================================================================== */

    private void handleRun(ActionEvent e) {
        // reset the marker that a TODO stub was called
        NotImplemented.Status.clear();
        /* ---------- read controls ------------------------------------- */
        int m         = sizeSlider.getValue();
        int nOps      = (Integer) opsSpin.getValue();
        int insertPct = ratioSlider.getValue();
        char hf       = hashCodeFor(hashBox.getSelectedIndex());
        String style  = styleBox.getSelectedItem().toString().toLowerCase(Locale.ROOT);
        String pattern= dataBox.getSelectedItem().toString().toLowerCase(Locale.ROOT);

        int minKey = (Integer) minKeySpin.getValue();
        int maxKey = (Integer) maxKeySpin.getValue();
        if (minKey >= maxKey) {
            JOptionPane.showMessageDialog(this,
                    "Minimum key must be less than maximum key.",
                    "Range Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* ---------- build table --------------------------------------- */
        table = switch (style) {
            case "separate chaining" -> new SeparateChainingHashTable(m, hf);
            case "linear"            -> new LinearProbingHashTable(m, hf);
            case "quadratic"         -> new QuadraticProbingHashTable(m, hf);
            case "double hash"       -> new DoubleHashingHashTable(m, hf);
            default -> throw new IllegalStateException("Unknown style");
        };

        int nInsert = (int) Math.round(nOps * (insertPct / 100.0));
        int nDelete = nOps - nInsert;
        int[] keys  = generateKeys(pattern, nInsert + nDelete, minKey, maxKey);

        /* ---------- debug print first keys ---------------------------- */
        stats.setText("Generated keys (first 25): " +
                java.util.Arrays.toString(java.util.Arrays.copyOf(keys, Math.min(keys.length, 25))) +
                System.lineSeparator());

        /* ---------- robust insert loop (handles probing saturation) --- */
        int inserted = 0;
        try {
            for (; inserted < nInsert; inserted++)
                table.insert(keys[inserted]);
        } catch (IllegalStateException full) {
            // Friendly pop‑up for immediate attention
            JOptionPane.showMessageDialog(this,
                    "Hash table became full after " + table.size() +
                            " successful inserts.",
                    "Table Full", JOptionPane.WARNING_MESSAGE);

            // Summarise in the sidebar so the run is recorded
            summaryField.setText(String.format(
                    "TABLE FULL after %d inserts (m=%d, load=%.3f)",
                    table.size(), m, (double) table.size() / m));

            // Skip the remaining stats and drawing; leave board as‑is
            board.setModel(table);
            return;
        }

        /* ---------- delete loop -------------------------------------- */
        nDelete = Math.min(nDelete, table.size());
        for (int i = 0; i < nDelete; i++)
            table.delete(keys[i]);

        /* ---------- check if any TODO stub was hit -------------------- */
        String todo = NotImplemented.Status.get();
        if (todo != null) {
            summaryField.setText("TODO: " + todo + " — finish this method!");
            stats.setText("Hash function not yet implemented; run aborted.");
            board.setModel(null);
            return;
        }

        /* ---------- stats -------------------------------------------- */
        double load = (double) table.size() / m;
        double rate = table.getInsertions() == 0
                      ? 0.0
                      : 100.0 * table.getCollisions() / table.getInsertions();
        int dups   = table.getDuplicates();
        summaryField.setText(String.format(
                "Collisions: %d   Inserts: %d   Duplicates: %d   Rate: %.2f%%",
                table.getCollisions(), table.getInsertions(), dups, rate));
        stats.append(String.format(Locale.ROOT,
                "m=%d  size=%d  load=%.3f  avg probes=%.3f%n",
                m, table.size(), load, table.averageProbes()));

        stats.append("\nFirst 15 buckets:\n");
        int view = Math.min(15, m);
        if (table instanceof SeparateChainingHashTable sch) {
            for (int i = 0; i < view; i++)
                stats.append(String.format("Slot %2d: %s%n", i,
                        sch.table[i].isEmpty() ? "empty" : sch.table[i]));
        } else if (table instanceof ProbingHashTable ph) {
            for (int i = 0; i < view; i++)
                stats.append(String.format("Slot %2d: %s%n", i,
                        ph.table[i] == null ? (ph.tombstone[i] ? "⊘" : "empty") : ph.table[i]));
        }

        board.setModel(table);
    }

    /* ================================================================== */
    /*  Random-data generators                                            */
    /* ================================================================== */

    private static int[] generateKeys(String pattern, int n, int min, int max) {
        return switch (pattern) {
            case "left-skewed"  -> skewed(n, true,  min, max);
            case "right-skewed" -> skewed(n, false, min, max);
            case "clustered"    -> cluster(n, min, max);
            case "bimodal"      -> bimodal(n, min, max);
            default             -> uniform(n, min, max);
        };
    }
    private static int[] uniform(int n, int min, int max) {
        return ThreadLocalRandom.current().ints(n, min, max + 1).toArray();
    }
    private static int[] skewed(int n, boolean left, int min, int max) {
        int mid = min + (max - min) / 2;
        int lo  = left ? min : mid, hi = left ? mid : max;
        return ThreadLocalRandom.current().ints(n, lo, hi + 1).toArray();
    }
    private static int[] cluster(int n, int min, int max) {
        Random rng = ThreadLocalRandom.current();
        int span = Math.max(1, (max - min) / 10);            // 10 % window
        int base = rng.nextInt(min, max - span + 1);
        return rng.ints(n, base, base + span + 1).toArray();
    }
    private static int[] bimodal(int n, int min, int max) {
        int[] a = skewed(n / 2, true,  min, max);
        int[] b = skewed(n - n / 2, false, min, max);
        int[] out = new int[n];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    /* ================================================================== */

    // HashGUI.java
    private static char hashCodeFor(int idx) {
        return switch (idx) {
            case 0 -> 'i';  // Division-modulo  ←  **changed**
            case 1 -> 'm';  // Multiplication
            case 2 -> 's';  // Mid-Square
            case 3 -> 'o';  // Folding
            case 4 -> 'f';  // Fibonacci
            case 5 -> 'r';  // Random
            case 6 -> 'c';  // Custom
            case 7 -> 'd';  // Some Other Hashing

            default -> 'r';
        };
    }

    /* ------------------------------------------------------------------ */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HashGUI::new);
    }
}