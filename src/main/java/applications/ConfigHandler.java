package applications;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    private final String PREFIX_DIR = "src/main/resources/data/";
    private String commandsFile     = "commands.txt";
    private String hashFunction     = "d";  // [d]ivision, [m]ultiplication, [r]andom, [f]olding, [c]ustom
    private String style           = "chain";   // chain | linear | quad | double
    private Integer hashSize        = 10;
    private Integer verbose         = 0;
    private int dataIndex;
    private int prefix              = 0;

    public ConfigHandler(String[] args ) {
        parseArgs(args);
    }

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    this.commandsFile = args[++i];
                    break;

                case "-h":
                    this.hashFunction = args[++i];
                    System.out.println("Setting Hash Function: " + getHashFunctionString());
                    break;

                case "-s": // String or Integer
                    this.hashSize = Integer.parseInt(args[++i]);
                    System.out.println("Setting Hash Size: " + this.hashSize );
                    break;

                case "-v": // String or Integer
                    this.verbose = Integer.parseInt(args[++i]);
                    System.out.println("Setting Verbose Level: " + this.verbose );
                    break;

                case "-t": // table style: chain, linear, quad, double
                    this.style = args[++i].toLowerCase();
                    System.out.println("Setting Table Style: " + this.style);
                    break;

                case "-p":
                        // -p only use for files in mvn resource/data directory
                        // otherwise relative path
                    this.prefix = Integer.parseInt(args[++i]);
                    System.out.println("Setting Prefix Level: " + this.prefix );
                    commandsFile    = PREFIX_DIR + commandsFile;
                    break;
            }
        }
    }

    public String getHashFunction() {
        return hashFunction;
    }

    public Integer getHashSize() {
        return hashSize;
    }

    public String getCommandsFile() {
        return commandsFile;
    }

    public int getDataIndex() {
        return dataIndex; // Default or specified index for data processing
    }

    public String getHashFunctionString() {
        switch (this.hashFunction) {
            case "d":
                return "division";
            case "m":
                return "multiplication";
            case "r":
                return "random";
            case "f":
                return "folding";
            case "c":
                return "custom";
            default:
                return "unknown"; // Handle unexpected cases
        }
    }

    public Integer getVerbose() {
        return verbose;
    }

    public int getPrefix() {
        return prefix; // Default or specified index for data processing
    }

    public List<String[]> readCommands() throws IOException {
        List<String[]> commands = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.commandsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split each line using whitespace to separate the command from its parameters
                commands.add(line.split("\\s+"));
            }
        } catch (IOException e) {
            System.out.println("Error reading command file: " + e.getMessage());
            throw e;  // Optionally rethrow to handle further up the chain
        }
        return commands;
    }

    public String getStyle() {
        return style;
    }

}
