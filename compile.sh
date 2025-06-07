#!/bin/sh
# Compile all Java sources with Java 21
mkdir -p out
javac --release 21 -d out Classes/*.java
