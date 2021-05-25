:: James Sinclair
:: Batch script to run java wealth distribution models
:: list of runs to use
:: set runs=default 2 3 4 5 6 7 8 9 10 11 12
set runs=8
echo %runs%
:: set seeds = 0, 1, 19

:: for each run
echo test
for %%r in (%runs%) do (
    echo %%r
    :: for each random seed
    :: for 100 runs
    for /l %%s in (20, 1, 99) do (
    :: for /l %%s in (0, 1, 19) do (
        java -classpath out World props/wealth-distrib-%%r.properties %%s
    )
)