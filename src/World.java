import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Singleton class that represents the World, which runs the simulation
 * and holds all turtles and patches for the wealth distribution simulation
 * Based on NetLogo wealth distribution model:
 * Wilensky, U. (1998). NetLogo Wealth Distribution model.
 * http://ccl.northwestern.edu/netlogo/models/WealthDistribution.
 * Center for Connected Learning and Computer-Based Modeling, Northwestern University, Evanston, IL.
 */
public class World {
    // singleton instance
    private static World instance;
    private static String propertiesFile = "props/wealth-distrib-default.properties";

    // turtles (agents) in the world
    private final List<Turtle> turtles = new ArrayList<>();
    // patches (divisions) of the world
    private Patch[][] patches;

    // current values of the lorenz curve
    private List<Float> lorenz;
    // historical value of the gini coefficient
    private final List<Float> gini = new ArrayList<>();

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
    private String csvFile;

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
     * @throws IOException when reading properties file
     */
    private void setupProperties(String propertiesFile) throws IOException {
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

        File propsFile = new File(propertiesFile);
        String basename = propsFile.getName().split("\\.")[0];
        csvFile = basename + ".csv";
        System.out.println("Output csv: " + csvFile);
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

        // setup properties
        // check if properties file passed as command-line argument
        if (args.length == 1) {
            propertiesFile = args[0];
        }

        world.setup(propertiesFile);

        // run model
        System.out.println("Running simulation");
        for (int i = 0; i < MAX_TICKS; i++) {
            System.out.println("Tick " + i);
            world.go();
            //world.printGrain();
        }
        // write results to csv
        world.writeToCsv();
    }

    private void writeToCsv() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            // print header
            pw.println("tick,gini");
            // print each line
            for (int g = 0; g < gini.size(); g++) {
                pw.println(String.format("%s,%s", g, gini.get(g)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printGrain() {
        for (int x = 0; x < X_PATCHES; x++) {
            for (int y = 0; y < Y_PATCHES; y++) {
                System.out.print(getPatch(x,y).grainString());
            }
            System.out.println();
        }
    }

    public void setup() {
        setup(propertiesFile);
    }

    public void setup(String propertiesFile) {
        System.out.println("Reading properties file " + propertiesFile);
        try {
            setupProperties(propertiesFile);
        } catch (IOException e) {
            System.err.println("Failed to read properties file " + propertiesFile);
            System.exit(1);
        }
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
        List<Patch> maxGrainPatches = new ArrayList<>();
        for (int x = 0; x < X_PATCHES; x++) {
            patches[x] = new Patch[Y_PATCHES];
            for (int y = 0; y < Y_PATCHES; y++) {
                // determine how much grain to seed patch with
                int patchGrain = determinePatchGrain();
                // create the new patch
                patches[x][y] = new Patch(x, y, patchGrain, NUM_GRAIN_GROWN);
                // store patches with max grain for convenience later
                if (patchGrain != 0) {
                    maxGrainPatches.add(patches[x][y]);
                }
            }
        }

        // spread grain around.  put some back into best land (diffuse)
        // diffuse 5 times
        printGrain();
        for (int i = 0; i < 5; i++) {
            for (Patch p: maxGrainPatches) {
                // reset to initial grain value
                p.setGrainHere(p.getMaxGrainHere());
                diffuseGrain(p, 0.25f);
            }
            System.out.println("Diffusion 1." + (i+1));
            printGrain();
        }

        // diffuse 10 times across all patches
        for (int i = 0; i < 10; i++) {
            for (int x = 0; x < X_PATCHES; x++) {
                for (int y = 0; y < Y_PATCHES; y++) {
                    diffuseGrain(getPatch(x,y), 0.25f);
                }
            }
            System.out.println("Diffusion 2." + (i+1));
            printGrain();
        }

        // update max grain
        for (int x = 0; x < X_PATCHES; x++) {
            for (int y = 0; y < Y_PATCHES; y++) {
                Patch p = getPatch(x,y);
                p.setMaxGrainHere(p.getGrainHere());
            }
        }
        System.out.println("Reset max grain:");
        printGrain();
    }

    /**
     * Diffuses grain around a central patch.  Takes proportion of the central
     * patches grain, and shares equally among neighbours.
     * Based on NetLogo diffuse, see:
     * http://ccl.northwestern.edu/netlogo/docs/dict/diffuse.html
     * @param centrePatch to diffuse grain from
     * @param proportion of grain to diffuse
     */
    private void diffuseGrain(Patch centrePatch, float proportion) {
        int centreX = centrePatch.X;
        int centreY = centrePatch.Y;
        // figure out how much grain to spread
        float grainToShare = centrePatch.getGrainHere()*proportion;
        int grainPerNeighbour = (int)Math.floor(grainToShare/8.0f);
        // remove grain from centre patch
        centrePatch.addGrain(-8*grainPerNeighbour);
        List<Patch> neighbours = getPatchNeighbours(centreX, centreY);
        // add grain to each neighbour
        for (Patch n: neighbours) {
            n.addGrain(grainPerNeighbour);
        }
    }

    /**
     * TODO
     */
    private void setInitialTurtleVars() {

    }

    /**
     * TODO
     */
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
        List<Integer> wealth = new ArrayList<>();
        for (Turtle turtle: turtles) {
            wealth.add(turtle.getWealth());
        }

        // lorenz points
        lorenz = computeLorenz(wealth);

        // gini
        float currentGini = updateGini(lorenz);
        gini.add(currentGini);
    }

    private List<Float> computeLorenz(List<Integer> wealth) {
        // sort wealth ascending
        Collections.sort(wealth);

        // calculate total wealth
        double totalWealth = wealth.stream()
                .mapToDouble(w -> w)
                .sum();
        List<Float> lor = new ArrayList<>();

        // compute Lorenz points
        int cumulativeWealth = 0;
        for (int w: wealth) {
            cumulativeWealth += w;
            float lorenzPoint = cumulativeWealth/(float)totalWealth;
            lor.add(lorenzPoint);
        }
        return lor;
    }

    private float updateGini(List<Float> lorenz) {
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

    /**
     * @return current tick of simulation
     */
    public int getTick() {
        return tick;
    }

    /**
     * Get patch, wrapping coordinates both horizontally and vertically
     * @param x x coordinate of patch
     * @param y y coordinate of patch
     * @return corresponding patch at (x,y)
     */
    public Patch getPatch(int x, int y) {
        int wrappedX = wrap(x, X_PATCHES);
        int wrappedY = wrap(y, Y_PATCHES);
        return patches[wrappedX][wrappedY];
    }

    /**
     * wrap v between 0 and (bound-1)
     * @param v value to wrap
     * @param bound maximum bound
     * @return wrapped value
     */
    private int wrap(int v, int bound) {
        int max = bound-1;
        if (v > max) {
            v = v % max - 1;
        } else if (v < 0) {
            v = bound+v;
        }
        return v;
    }

    public int getMAX_VISION() {
        return MAX_VISION;
    }

    public int getMETABOLISM_MAX() {
        return METABOLISM_MAX;
    }

    public int getLIFE_EXPECTANCY_MIN() {
        return LIFE_EXPECTANCY_MIN;
    }

    public int getLIFE_EXPECTANCY_MAX() {
        return LIFE_EXPECTANCY_MAX;
    }

    /**
     * Get patches a particular distance from centre patch in a particular
     * heading
     * @param centreX x coordinate of centre patch
     * @param centreY y coordinate of centre patch
     * @param heading direction to list patches
     * @param distance number of patches to list in direction
     * @return list of patches in a direction up to distance from  centre point
     * TODO return all patches in the heading direction within the turtle's vision. If the vision reach the boundary of the world, then just return the available patches in the heading direction that is in the world
     */
    public List<Patch> getHeadingPatches(int centreX, int centreY, Heading heading, int distance) {
        //TODO: please put the first heading patch in the first position
        // of the arrayList, this element will be used in the Turtle class
        // to determine the next patch the turtle should move to.

        List<Patch> neighbours = new ArrayList<>();
        int xMin = 0;
        int xMax = 0;
        int yMin = 0;
        int yMax = 0;
        // determine bounds for loop below
        if (heading == Heading.NORTH) {
            xMin = centreX;
            xMax = centreX;
            yMin = centreY + 1;
            yMax = yMin + distance - 1;
        } else if (heading == Heading.SOUTH) {
            xMin = centreX;
            xMax = centreY;
            yMax = centreY - 1;
            yMin = yMax - distance + 1;
        } else if (heading == Heading.EAST) {
            xMin = centreX + 1;
            xMax = xMin + distance - 1;
            yMin = centreY;
            yMax = centreY;
        } else if (heading == Heading.WEST) {
            xMax = centreX - 1;
            xMin = xMax - distance + 1;
            yMin = centreY;
            yMax = centreY;
        }

        // get list of neighbours
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                neighbours.add(getPatch(x,y));
            }
        }

        return neighbours;
    }

    /**
     * get immediate neighbours of patch[x][y]
     * @param centreX x coordinate of patch
     * @param centreY y coordinate of patch
     * @return ArrayList of neigbouring patches
     */
    public List<Patch> getPatchNeighbours(int centreX, int centreY) {
        List<Patch> neighbours = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
           for (int y = -1; y <= 1; y++) {
               // add all except centre patch
               if (!(x == 0 && y == 0)) {
                   neighbours.add(getPatch(centreX+x, centreY+y));
               }
           }
        }
        return neighbours;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * Get the location of the next patch by moving from (centreX, centreY) in
     * the direction heading.
     * @param centreX X coordinate of current patch
     * @param centreY Y coordinate of current patch
     * @param direction to head
     * @return point with (x,y) coordinates of new location
     */
    public Point getNextPatch(int centreX, int centreY, Heading direction) {
        int newCentreX = centreX;
        int newCentreY = centreY;

        // increment coordinates in accordance with direction
        if (direction == Heading.NORTH) {
            newCentreY += 1;
        } else if (direction == Heading.EAST) {
            newCentreX += 1;
        } else if (direction == Heading.SOUTH) {
            newCentreY -= 1;
        } else if (direction == Heading.WEST) {
            newCentreX -= 1;
        }

        // wrap coordinates in case we have reached bounds of map
        newCentreX = wrap(newCentreX, X_PATCHES);
        newCentreY = wrap(newCentreY, Y_PATCHES);

        // return corresponding point
        return new Point(newCentreX, newCentreY);
    }
}

