for /l %%x in (0, 1, 19) do (
    java -classpath out World props/wealth-distrib-default.properties %%x
)