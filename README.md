# mcss-assignment-2
SWEN90004 - Modelling Complex Software Systems - Assignment 2

## Properties Files

- Properties files: live in `props/`

## Build

To build the simulation:
```bash
javac -d out src/*.java --class-path=src
```

## Run

To run the simulation:
```bash
java -classpath out World
```
- can pass properties file as command line argument, otherwise defaults
  to `props/wealth-distrib-default.properties`
