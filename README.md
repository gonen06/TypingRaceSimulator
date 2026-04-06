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

The race is started by calling `startRace()` on a `TypingRace` object.
A simple way to test this is to add a `main` method to `TypingRace`, for example:

```java
public static void main(String[] args) {
    TypingRace race = new TypingRace(40);
    race.addTypist(new Typist('①', "TURBOFINGERS", 0.85), 1);
    race.addTypist(new Typist('②', "QWERTY_QUEEN",  0.60), 2);
    race.addTypist(new Typist('③', "HUNT_N_PECK",   0.30), 3);
    race.startRace();
}
```

Then run:

```bash
java TypingRace
```

## Part 2 — GUI Simulation

This folder contains the graphical version of the simulation. It includes the complete GUI interface for the program.

### How to run

As required, the graphical version is started by calling startRaceGUI(). The TypingRace class provides a main method that automatically triggers this graphical entry point.

Simply run:
```java
java TypingRace
```

Then run:

```bash
javac Typist.java TypingRaceGUI.java TypingRace.java
```



## Dependencies

- Java Development Kit (JDK) 11 or higher
- No external libraries required for Part 1
- Part 2 uses Java Swing (included in standard JDK)

## Notes

- All code should compile and run using standard command-line tools without any IDE-specific configuration.
- The bugs has been fixed. Feel free to contribute to the project.
