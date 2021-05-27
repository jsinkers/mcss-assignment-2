:: SWEN90004 Assignment 2 - Wealth Distribution
:: James Sinclair - 1114278, Yujun Yan - 952112, Junkai Xing - 1041973
:: Batch script to build and run java wealth distribution models

:: build model
javac -d out src/*.java --class-path=src

:: list of runs to use
set runs=default 2 3 4 5 6 7 8 9 10 11 12
set inheritance=true false

:: with/without inheritance
for %%i in (%inheritance%) do (
    :: for each run
    for %%r in (%runs%) do (
        echo %%r
        :: for each random seed
        for /l %%s in (0, 1, 100) do (
            java -classpath out World props/wealth-distrib-%%r.properties %%s %%i
        )
    )
)