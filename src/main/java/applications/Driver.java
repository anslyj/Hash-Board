package applications;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import datastructures.HashTable;
import datastructures.SimpleHashTable;
import datastructures.ProbingHashTable;
import datastructures.SeparateChainingHashTable;
import datastructures.ProbeType;

public class Driver {
    private static void processCommands(HashTable hashTable, ConfigHandler config) {
        try {
            List<String[]> commands = config.readCommands();
            for (String[] parts : commands) {
                hashTable.setVerbose(config.getVerbose());
                // Check if the command needs additional arguments and skip if not provided
                Integer key = null;
                if (parts.length > 1 ) {
                    try {
                        key = Integer.parseInt(parts[1]); // Convert string to integer
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format for command: " + parts[0] + " " + parts[1]);
                        continue; // Skip this command due to invalid number format
                    }
                }

                if (config.getVerbose() > 5) {
                    // demonstrating verbose level
                    System.out.println("Processing command: " + parts[0] );
                }
                switch (parts[0]) {
                    case "insert":
                        boolean inserted = hashTable.insert(key);
                        if( config.getVerbose() > 1 ) { System.out.println("Insert " + parts[1] + (inserted ? "" : " failed")); }
                        break;
                    case "delete":
                        boolean deleted = hashTable.delete(key);
                        if( config.getVerbose() > 1 ) { System.out.println("Delete " + parts[1] + (deleted ? "" : " failed") ); }
                        break;
                    case "search":
                        boolean found = hashTable.find(key) != null;
                        if( config.getVerbose() > 1 ) { System.out.println("Search " + parts[1] + (found ? "found" : " failed") ); }
                        break;
                    case "print":
                        hashTable.print();
                        break;
                    default:
                        System.out.println("Unknown command: " + parts[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read commands: " + e.getMessage());
        }
    }

    public static void printArgList( ConfigHandler config, String[] args ) {
        if ( config.getPrefix() > -1) {
            System.out.print("\tCommand line:            ");
            for (String arg : args) {
                System.out.print(arg + " ");
            }
            System.out.println();
        }
    }

    public static void printSettings(ConfigHandler config, String[] args ) {
        System.out.println("\nSettings:");
        printArgList( config, args );
        System.out.println("\tCommand file name:       " + config.getCommandsFile());
        System.out.println("\tHash function:           " + config.getHashFunctionString());
        System.out.println("\tHash size:               " + config.getHashSize());
        System.out.println("\tTable style:             " + config.getStyle());
        System.out.println("\tVerbose level :          " + config.getVerbose());
    }

    /**
     * Factory for whichever table style the instructor selects in the command file
     * or on the command‑line.
     */
    private static HashTable createHashTable(ConfigHandler config) {
        int    size   = config.getHashSize();
        char   hashFn = config.getHashFunctionString().charAt(0);  // 'd','m','f',…
        String style = (config.getStyle() == null || config.getStyle().isBlank())
                       ? "chain"
                       : config.getStyle().toLowerCase(Locale.ROOT);   // chain | linear | quad | double

        return switch (style) {
            case "chain"  -> new SeparateChainingHashTable(size, hashFn);
            case "linear" -> new ProbingHashTable(size, ProbeType.LINEAR, hashFn);
            case "quad"   -> new ProbingHashTable(size, ProbeType.QUADRATIC, hashFn);
            case "double" -> new ProbingHashTable(size, ProbeType.DOUBLE_HASHING, hashFn);
            default       -> new SimpleHashTable(size);            // safe fallback
        };
    }

    public static void main(String[] args) {
        ConfigHandler config = new ConfigHandler( args );
        Integer verbose = config.getVerbose();
        HashTable hashTable = createHashTable(config);
        processCommands(hashTable, config);  // Process input commands
        printSettings(config, args );
    }
}
