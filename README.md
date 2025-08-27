Hashing Lab (Java)

Interactive Java project to learn/compare hash tables. Implements separate chaining and open addressing (linear, quadratic, double hashing) with swappable hash functions. Includes a Swing GUI visualizer and a CLI for scripted runs.

Features

Chaining + open addressing (linear/quadratic/double)

Pluggable hash functions (division, multiplicative, Fibonacci, mid-square, folding, random, custom)

Metrics: collisions, duplicates, average probes

Run (Maven)
mvn -q compile
# GUI
mvn -q exec:java -Dexec.mainClass=gui.HashGUI
# CLI
mvn -q exec:java -Dexec.mainClass=applications.Driver \
  -Dexec.args="-t chain -h d -s 17 -c src/main/resources/data/commands.txt"

Commands file (example)
insert 42
search 42
print

Layout
src/main/java/{applications,datastructures,gui}
src/main/resources/data/commands.txt
