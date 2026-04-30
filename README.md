# TypingRaceSimulator

Object Oriented Programming Project — ECS414U

## Project Structure

```
TypingRaceSimulator/
├── Part1/    # Textual simulation (Java, command-line)
└── Part2/    # GUI simulation (Java Swing)
```

## Part 1 — Textual Simulation

### How to compile

```bash
cd Part1
javac Typist.java TypingRace.java
```

### How to run

```bash
java TypingRace
```

## Part 2 — GUI Simulation

This folder contains the graphical version of the simulation. It includes the complete GUI interface for the program.


### How to compile

```bash
cd Part2
javac Typist.java TypingRaceGUI.java TypingRace.java
```


### How to run

As required, the graphical version is started by calling startRaceGUI(). The TypingRace class provides a main method that automatically triggers this graphical entry point.

Simply run:
```bash
java TypingRace
```


## Dependencies

- Java Development Kit (JDK) 11 or higher
- No external libraries required for Part 1
- Part 2 uses Java Swing (included in standard JDK)

## Notes

- All code should compile and run using standard command-line tools without any IDE-specific configuration.
- The bugs have been fixed. Feel free to contribute to the project.
