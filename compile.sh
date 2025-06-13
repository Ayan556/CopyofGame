#!/bin/sh
# Compile all Java sources with Java 23
mkdir -p out
javac --release 23 -d out Classes/*.java
