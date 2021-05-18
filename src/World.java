import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

/**
 * Singleton class that represents the World, running the simulation
 */
public class World {
    // singleton instance
    private static World instance;
    private static String propertiesFile = "props/wealth-distrib-default.properties";

    // turtles (agents) in the world
    private final ArrayList<Turtle> turtles = new ArrayList<>();
    // patches (divisions) of the world
    private Patch[][] patches;

    // current values of the lorenz curve
    private ArrayList<Float> lorenz;
    // current value of the gini coefficient
    private float gini;

    // current tick of the world
    private int tick;
    // number of iterations to run simulation for
    private static int MAX_TICKS;
    private int X_PATCHES;
    private int Y_PATCHES;
    private int NUM_PEOPLE;
    private int METABOLISM_MAX;
    private int MAX_VISION;
    private int LIFE_EXPECTANCY_MIN;
    private int LIFE_EXPECTANCY_MAX;
    private int PERCENT_BEST_LAND;
    private int NUM_GRAIN_GROWN;
    private int GRAIN_GROWTH_INTERVAL;
    private int RANDOM_SEED;

    // maximum grain any patch can hold
    private int MAX_GRAIN;

    private Random random;


    public World() {
        // read parameters from config file
        MAX_GRAIN = 50;
    }

    /**
     * Read simulation properties from a properties file
     * @param propertiesFile to read properties from
     * @return Properties with loaded values
     * @throws IOException when reading properties file
     */
    private Properties setupProperties(String propertiesFile) throws IOException {
        Properties worldProperties = new Properties();
        try (FileReader inStream = new FileReader(propertiesFile)) {
            worldProperties.load(inStream);
        }

        MAX_TICKS = Integer.parseInt(worldProperties.getProperty("MaxTicks"));
        X_PATCHES = Integer.parseInt(worldProperties.getProperty("XPatches"));
        Y_PATCHES = Integer.parseInt(worldProperties.getProperty("YPatches"));
        NUM_PEOPLE = Integer.parseInt(worldProperties.getProperty("NumPeople"));
        MAX_VISION = Integer.parseInt(worldProperties.getProperty("MaxVision"));
        METABOLISM_MAX = Integer.parseInt(worldProperties.getProperty( "MetabolismMax"));
        LIFE_EXPECTANCY_MIN = Integer.parseInt(worldProperties.getProperty( "LifeExpectancyMin"));
        LIFE_EXPECTANCY_MAX = Integer.parseInt(worldProperties.getProperty( "LifeExpectancyMax"));
        PERCENT_BEST_LAND = Integer.parseInt(worldProperties.getProperty( "PercentBestLand"));
        NUM_GRAIN_GROWN = Integer.parseInt(worldProperties.getProperty( "NumGrainGrown"));
        GRAIN_GROWTH_INTERVAL = Integer.parseInt(worldProperties.getProperty( "GrainGrowthInterval"));
        RANDOM_SEED = Integer.parseInt(worldProperties.getProperty( "RandomSeed"));
        random = new Random(RANDOM_SEED);

        return worldProperties;
    }

    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    public static void main(String[] args) {
        // create and setup world
        World world = World.getInstance();
        if (args.length == 1) {
            propertiesFile = args[0];
        }

        // setup properties
        System.out.println("Reading properties file " + propertiesFile);
        try {
            world.setupProperties(propertiesFile);
        } catch (IOException e) {
            System.err.println("Failed to read properties file " + propertiesFile);
            System.exit(1);
        }

        // determine output filename from properties filename
        String basename = propertiesFile.split("\\.")[0];
        String csvName = basename + ".csv";

        world.setup();

        // run model
        System.out.println("Running simulation");
        for (int i = 0; i < MAX_TICKS; i++) {
            System.out.println("Tick " + i);
            world.go();
            // update statistics
            world.printGrain();

            // write results to csv
        }
    }

    private void printGrain() {
        for (int x = 0; x < X_PATCHES; x++) {
            for (int y = 0; y < Y_PATCHES; y++) {
                System.out.print(patches[x][y].getGrainHere() + " ");
            }
            System.out.println();
        }
    }

    public void setup() {
        // set up patches
        setupPatches();
        // set up turtles
        setupTurtles();
        updateLorenzAndGini();
    }

    /**
     * Creates patches and initialises with grain
     */
    private void setupPatches() {
        // create array of patches
        // some patches can hold the highest amount of grain possible (best
        // land)
        patches = new Patch[X_PATCHES][Y_PATCHES];

        // initialise patches
        for (int x = 0; x < X_PATCHES; x++) {
            for (int y = 0; y < Y_PATCHES; y++) {
                int patchGrain = determinePatchGrain();
                patches[x][y] = new Patch(patchGrain, NUM_GRAIN_GROWN); }
        }

        // TODO: spread grain around.  put some back into best land (diffuse)

    }

    private void setInitialTurtleVars() {

    }

    private void setupTurtles() {

    }

    private void go() {
        // run each turtle

        // run each patch
        // grow grain if necessary
        if (tick % GRAIN_GROWTH_INTERVAL == 0) {
            for (int x = 0; x < X_PATCHES; x++) {
                for (int y = 0; y < Y_PATCHES; y++) {
                    patches[x][y].growGrain();
                }
            }
        }

        updateLorenzAndGini();
    }

    private void updateLorenzAndGini() {
        // determine wealth of each turtle
        ArrayList<Integer> wealth = new ArrayList<>();
        for (Turtle turtle: turtles) {
            wealth.add(turtle.getWealth());
        }

        // lorenz points
        lorenz = computeLorenz(wealth);

        // gini
        gini = updateGini(lorenz);
    }

    private ArrayList<Float> computeLorenz(ArrayList<Integer> wealth) {
        // sort wealth ascending
        Collections.sort(wealth);

        // calculate total wealth
        double totalWealth = wealth.stream()
                .mapToDouble(w -> w)
                .sum();
        ArrayList<Float> lor = new ArrayList<>();

        // compute Lorenz points
        int cumulativeWealth = 0;
        for (int w: wealth) {
            cumulativeWealth += w;
            float lorenzPoint = cumulativeWealth/(float)totalWealth;
            lor.add(lorenzPoint);
        }
        return lor;
    }

    private float updateGini(ArrayList<Float> lorenz) {
        float giniIndex = 0;
        for (int i = 0; i < lorenz.size(); i++) {
            giniIndex += (i+1)/(float)lorenz.size() - lorenz.get(i);
        }
        return giniIndex;
    }

    /**
     * Determine how much grain each patch should be seeded with
     */
    private int determinePatchGrain() {
        int patchGrain = 0;
        // check if this is best land
        if (random.nextFloat() <= (PERCENT_BEST_LAND/100.0)) {
            patchGrain = MAX_GRAIN;
        }
        return patchGrain;
    }

    public int getTick() {
        return tick;
    }

    /**
     * Get patch, wrapping coordinates
     * @param x x coordinate of patch
     * @param y y coordinate of patch
     * @return corresponding patch at (x,y)
     */
    public Patch getPatch(int x, int y) {
        return patches[wrap(x, X_PATCHES)][wrap(y, Y_PATCHES)];
    }

    /**
     * wrap v between 0 and max
     * @param v value to wrap
     * @param max maximum value
     * @return wrapped value
     */
    private int wrap(int v, int max) {
        if (v >= max) {
            v = v % max;
        } else if (v < 0) {
            v = max-v;
        }
        return v;
    }

    public ArrayList<Patch> getHeadingPatches(int x, int y, int heading, int distance) {
        ArrayList<Patch> neighbours = new ArrayList<>();
        // north
        if (heading == 0) {

        } else if (heading == 90) {
            // east

        } else if (heading == 180) {
            // south

        } else if (heading == 270) {
            // west

        }
        return neighbours;
    }

    /**
     * get immediate neighbours of patch[x][y]
     * @param centreX x coordinate of patch
     * @param centreY y coordinate of patch
     * @return ArrayList of neigbouring patches
     */
    public ArrayList<Patch> getPatchNeighbours(int centreX, int centreY) {
        ArrayList<Patch> neighbours = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
           for (int y = -1; y <= 1; y++) {
               if (x != 0 && y != 0) {
                   neighbours.add(getPatch(centreX+x, centreY+y));
               }
           }
        }
        return neighbours;
    }
}

