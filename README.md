# Wealth Distribution

SWEN90004 - Modelling Complex Software Systems - Assignment 2
James Sinclair - 1114278, Yujun Yan - 952112, Junkai Xing - 1041973

## Data Analysis

- Jupyter notebook with full details of data analysis

## Organisation

Directory structure:

- `data-analysis`: Jupyter notebooks and output from data analysis
- `java-data`:
  - commit-{hash}: stores Java output from a given commit
    -  `initial-patches`: stores initial grain state of patches
- `netlogo-data`: BehaviorSpace output of NetLogo wealth distribution model
    - `initial-world-check`: stores initial grain state of patches for NetLogo models
- `out`
- `props`: properties files as described below
- `src`: source code for Java model

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

- arg 1: properties filename {string}
  - default value = `props/wealth-distrib-default.properties`
  - e.g. `java -classpath out World props/wealth-distrib-default.properties`
- arg 2: random number seed {int}
  - default value = 0
  - If provided, must also pass arg 1.
  - e.g. `java -classpath out World props/wealth-distrib-default.properties 10`
- arg 3: whether to run models with inheritance extension {boolean}
  - default value = false (no inheritance)
  - can only be used with arg 1 and arg 2

## Running experiments

Run the Windows batch script `wealth-distrib.bat` to build and then run experiments.
An individual CSV will be output for each run of each experiment.

## Tests

JUnit is required to run unit tests. Requires manual setup.  Build is
not currently supported.
