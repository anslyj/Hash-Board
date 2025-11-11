# Hashing Lab (Java)

An interactive Java educational project designed to help students learn and compare different hash table implementations and collision resolution strategies. This comprehensive lab provides both a visual Swing GUI for interactive experimentation and a command-line interface for scripted testing and automation.

## Overview

This project implements multiple hash table data structures with various collision resolution techniques and hash functions. It serves as a practical learning tool for understanding how different hashing strategies perform under various conditions, making it ideal for computer science courses covering data structures and algorithms.

## Features

### Collision Resolution Methods

The project supports four distinct collision resolution strategies:

- **Separate Chaining**: Uses linked lists at each bucket to handle collisions, allowing multiple keys to hash to the same index
- **Linear Probing**: Resolves collisions by sequentially searching for the next available slot in the table
- **Quadratic Probing**: Uses a quadratic function to find the next available slot, reducing primary clustering
- **Double Hashing**: Employs a secondary hash function to determine probe sequences, providing excellent distribution

### Pluggable Hash Functions

The system includes a flexible architecture that supports multiple hash function implementations:

- **Division Method** (`d`): Classic modulo-based hashing using `key % m`
- **Multiplicative Method** (`m`): Knuth's multiplicative hash using the golden ratio
- **Fibonacci Hashing** (`f`): Utilizes Fibonacci numbers for distribution
- **Mid-Square Method** (`s`): Squares the key and extracts middle digits
- **Folding Method** (`o`): Splits the key into groups and sums them
- **Random Hashing** (`r`): Uses a seeded random number generator
- **Custom Hash** (`c`): User-defined hash function implementation

### Performance Metrics

The implementation tracks comprehensive statistics to analyze hash table performance:

- **Collision Count**: Total number of collisions encountered during operations
- **Duplicate Detection**: Count of attempted insertions of existing keys
- **Average Probe Length**: Mean number of probes required for successful searches
- **Load Factor**: Current utilization of the hash table (size / capacity)
- **Insertion Rate**: Percentage of insertions that resulted in collisions

### Interactive GUI

The Swing-based graphical interface (`HashGUI`) provides an intuitive way to experiment with different configurations:

- Visual representation of hash table state
- Real-time statistics display
- Configurable table size (5-201 slots)
- Adjustable operation counts and insert/delete ratios
- Multiple data distribution patterns (uniform, skewed, clustered, bimodal)
- Customizable key ranges for testing

### Command-Line Interface

The CLI (`Driver`) enables scripted testing and automation:

- Process commands from text files
- Configurable verbosity levels for debugging
- Support for batch operations
- Easy integration with testing frameworks

## Installation & Requirements

### Prerequisites

- Java 23 or higher
- Apache Maven 3.6+ (for building and running)

### Building the Project

Compile the project using Maven:

```bash
mvn -q compile
```

## Usage

### Running the GUI

Launch the interactive graphical interface:

```bash
mvn -q exec:java -Dexec.mainClass=gui.HashGUI
```

The GUI allows you to:
1. Select collision resolution method (chaining, linear, quadratic, double)
2. Choose hash function from the dropdown
3. Adjust table size and operation parameters
4. Select data distribution patterns
5. Run simulations and view visual results

### Running the CLI

Execute the command-line driver with custom parameters:

```bash
mvn -q exec:java -Dexec.mainClass=applications.Driver \
  -Dexec.args="-t chain -h d -s 17 -c src/main/resources/data/commands.txt"
```

#### Command-Line Arguments

- `-t <style>`: Table style (`chain`, `linear`, `quad`, `double`)
- `-h <function>`: Hash function (`d`, `m`, `f`, `s`, `o`, `r`, `c`)
- `-s <size>`: Hash table size (number of buckets)
- `-c <file>`: Path to commands file
- `-v <level>`: Verbosity level (0-10, higher = more output)
- `-p`: Enable prefix output mode

### Commands File Format

Create a text file with one command per line:

```
insert 42
insert 15
insert 42
search 15
delete 42
print
```

Supported commands:
- `insert <key>`: Insert an integer key into the hash table
- `delete <key>`: Remove a key from the hash table
- `search <key>`: Search for a key and report if found
- `print`: Display the current state of the hash table

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── applications/      # CLI driver and configuration
│   │   │   ├── Driver.java
│   │   │   └── ConfigHandler.java
│   │   ├── datastructures/    # Hash table implementations
│   │   │   ├── HashTable.java
│   │   │   ├── SeparateChainingHashTable.java
│   │   │   ├── ProbingHashTable.java
│   │   │   ├── LinearProbingHashTable.java
│   │   │   ├── QuadraticProbingHashTable.java
│   │   │   ├── DoubleHashingHashTable.java
│   │   │   └── HashFunctions.java
│   │   └── gui/               # Swing GUI components
│   │       ├── HashGUI.java
│   │       └── Board.java
│   └── resources/
│       └── data/
│           └── commands.txt    # Example commands file
└── test/
    └── java/                   # Test files (if any)
```

## Educational Value

This project is designed to help students:

1. **Understand Hash Table Fundamentals**: Learn how hash tables work internally
2. **Compare Collision Strategies**: See firsthand how different resolution methods perform
3. **Evaluate Hash Functions**: Experiment with various hashing algorithms and their distributions
4. **Analyze Performance**: Use metrics to understand trade-offs between methods
5. **Visualize Data Structures**: See hash table state changes in real-time through the GUI

## Example Workflow

1. Start the GUI: `mvn -q exec:java -Dexec.mainClass=gui.HashGUI`
2. Select "Separate Chaining" as the table style
3. Choose "Division" hash function
4. Set table size to 20
5. Set operations to 50 with 100% inserts
6. Select "Uniform" data distribution
7. Click "Run" to see the hash table fill and view statistics
8. Experiment with different configurations to observe performance differences

## Technical Details

- **Language**: Java 23
- **Build Tool**: Apache Maven
- **GUI Framework**: Java Swing
- **Architecture**: Object-oriented design with inheritance and polymorphism
- **Hash Table Interface**: Abstract base class `HashTable` with concrete implementations

## Contributing

This is an educational project. When extending functionality:

1. Follow the existing code structure and naming conventions
2. Implement hash functions in `HashFunctions.java`
3. Add new collision resolution methods by extending `HashTable` or `ProbingHashTable`
4. Update the GUI dropdowns if adding new options
5. Maintain comprehensive statistics tracking

## License

This project is intended for educational purposes as part of a computer science course.

