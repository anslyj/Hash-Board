package gui;

import datastructures.HashTable;
import datastructures.ProbingHashTable;
import datastructures.SeparateChainingHashTable;

import javax.swing.*;
import java.awt.*;

/**
 * Visualizes a hash table.
 *
 * • Open-address tables (ProbingHashTable) appear as one horizontal row of slots.
 *   – blue  = occupied
 *   – gray  = tombstone
 *   – white = empty
 *
 * • Separate-chaining tables appear as vertical bucket stacks, one column per bucket.
 *
 * Call setModel(table) whenever you rebuild / mutate the table, then repaint().
 */
public class Board extends JPanel {

    private HashTable model;

    /** Show numeric labels when the table has few buckets (≤ this). */
    private static final int MAX_LABEL_M = 30;


    public Board() {
        setPreferredSize(new Dimension(600, 320));
        setBackground(Color.WHITE);
    }

    /** Attach a hash-table model and refresh the drawing. */
    public void setModel(HashTable m) {
        this.model = m;
        repaint();
    }

    /* ------------------------------------------------------------------ */

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (model == null) return;

        if (model instanceof SeparateChainingHashTable sch)
            drawChaining(g, sch);
        else if (model instanceof ProbingHashTable ph)
            drawProbing(g, ph);
    }

    /* ---------- drawing helpers --------------------------------------- */

    /** Horizontal row for open-address probing tables. */
    private void drawProbing(Graphics g, ProbingHashTable ht) {
        int m      = ht.table.length;
        int usable = getWidth() - 20;
        int slotW  = Math.max(4, usable / m);      // keep visible when m huge
        int h      = getHeight() - 40;
        int y0     = 20;

        for (int i = 0; i < m; i++) {
            int x = 10 + i * slotW;
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(x, y0, slotW, h);

            if (ht.table[i] != null) {
                g.setColor(new Color(100, 170, 255));          // occupied
                g.fillRect(x + 1, y0 + 1, slotW - 1, h - 1);
            } else if (ht.tombstone[i]) {
                g.setColor(Color.GRAY);                        // tombstone
                g.fillRect(x + 1, y0 + 1, slotW - 1, h - 1);
            }

            // draw the key label if table is small
            if (m <= MAX_LABEL_M && ht.table[i] != null) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                String txt = ht.table[i].toString();
                int txtWidth = g.getFontMetrics().stringWidth(txt);
                /* right‑justify inside the slot (2‑px right padding) */
                int tx = x + slotW - txtWidth - 2;
                int ty = y0 + h / 2 + 5;
                g.drawString(txt, tx, ty);
            }
        }
    }

    /** Bucket columns for separate-chaining tables. */
    private void drawChaining(Graphics g, SeparateChainingHashTable ht) {
        int m        = ht.table.length;
        int usable   = getWidth() - 20;
        int bucketW  = Math.max(8, usable / m);
        int x        = 10;

        for (int i = 0; i < m; i++, x += bucketW) {
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, 10, bucketW, getHeight() - 20);

            var it = ht.table[i].iterator();
            int y  = 14;
            while (it.hasNext() && y < getHeight() - 14) {
                Integer key = it.next();
                g.setColor(new Color(140, 200, 255));
                g.fillRect(x + 3, y, bucketW - 6, 8);

                if (m <= MAX_LABEL_M && key != null) {
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                    int txtWidth = g.getFontMetrics().stringWidth(key.toString());
                    /* right‑justify within the bucket (4‑px right padding) */
                    int tx = x + bucketW - txtWidth - 4;
                    g.drawString(key.toString(), tx, y + 7);
                }
                y += 10;
            }
        }
    }
}