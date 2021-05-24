# Wealth Distribution

SWEN90004 - Modelling Complex Software Systems - Assignment 2

## Properties Files

- Properties files: live in `props/`

Properties descriptions `wealth-distrib-{i}.properties` for experiments.
(Changes from the NetLogo model settings are used unless specified below)

1. `wealth-distrib-default.properties`: Default
2. `wealth-distrib-2.properties`: num-people = 1000
3. `wealth-distrib-3.properties`: num-people = 50
4. `wealth-distrib-4.properties`: max-vision = 15
5. `wealth-distrib-5.properties`: max-vision = 1
6. `wealth-distrib-6.properties`: percent-best-land = 25%
7. `wealth-distrib-7.properties`: percent-best-land = 5%
8. `wealth-distrib-8.properties`: grain-growth-interval = 10
9. `wealth-distrib-9.properties`: num_grain_grown = 1
10. `wealth-distrib-10.properties`:  num_grain_grown = 10
11. `wealth-distrib-11.properties`:  life-expectancy-min = 50; life-expectancy-max = 50
12. `wealth-distrib-12.properties`:  life-expectancy-min = 50; life-expectancy-max = 100

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
- output CSV will be named as `{properties-file}-seed-{random-seed}.csv`

### Command Line Options

- arg 1: properties filename, default value = `props/wealth-distrib-default.properties`
  - e.g. `java -classpath out World props/wealth-distrib-default.properties`
- arg 2: random number seed, default value = 0.  If provided, must
  also pass arg 1.
  - e.g. `java -classpath out World props/wealth-distrib-default.properties 10`
