#!/bin/sh
# Compile all Java sources with Java 21
mkdir -p out
javac --release 21 -d out Classes/*.java

# Copy resources so they are on the runtime classpath
cp -r res/Audio out/
cp -r res/Fonts out/
cp -r res/images out/
