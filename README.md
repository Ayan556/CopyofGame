# CopyofGame

This project contains the source code for the game. No compiled class files are tracked in the repository.

## Building

The project expects Java 21. To compile all sources into the `out` directory run:

```bash
javac --release 21 -d out Classes/*.java
```

If you previously built the game using a newer JDK, remove the `out` directory first to avoid "bad class file" version errors.

## Running

After compiling, run the game using the main class:

```bash
java -cp out Main
```

The game uses Swing for its UI, so it requires a graphical environment.
