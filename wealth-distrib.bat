:: James Sinclair
:: Batch script to run java wealth distribution models

:: build model
javac -d out src/*.java --class-path=src

:: list of runs to use
set runs=default 2 3 4 5 6 7 8 9 10 11 12
set inheritance=true false

:: for each run
echo test
for %%r in (%runs%) do (
    echo %%r
    :: for each random seed
    for /l %%s in (0, 1, 100) do (
        for %%i in (%inheritance%) do (
            java -classpath out World props/wealth-distrib-%%r.properties %%s %%i
        )
    )
)