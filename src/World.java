import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Singleton class that represents the World, which runs the simulation
 * and holds all turtles and patches for the wealth distribution simulation.
 * Contains entrypoint for the simulation.
 * Based on NetLogo wealth distribution model:
 * Wilensky, U. (1998). NetLogo Wealth Distribution model.
 * http://ccl.northwestern.edu/netlogo/models/WealthDistribution.
 * Center for Connected Learning and Computer-Based Modeling, Northwestern
 * University, Evanston, IL.
 */
public class World {
    // singleton instance
    private static World instance;
    // number of iterations to run simulation for
    // TODO: no longer static
    private static int maxTicks;

    // turtles (agents) in the world
    private final List<Turtle> turtles = new ArrayList<>();
    // patches (divisions) of the world
    private Patch[][] patches;

    // current values of the lorenz curve
    private List<Float> lorenz;
    // historical value of the gini coefficient
    private final List<Float> gini = new ArrayList<>();

    // properties file used to load run parameters
    private String propertiesFile = "props/wealth-distrib-default.properties";

    // run parameters
    // current tick of the world
    private int tick;
    // number of patches in x direction
    private int xPatches;
    // number of patches in y direction
    private int yPatches;
    // number of people (turtles) to seed in the world
    private int numPeople;
    // maximum value of metabolism for turtles
    private int metabolismMax;
    // maximum number of patches ahead a turtle can see
    private int maxVision;
    // minimum turtle life expectancy
    private int lifeExpectancyMin;
    // maximum turtle life expectancy
    private int lifeExpectancyMax;
    // percentage of land that has maximum grain capacity
    private int percentBestLand;
    // quantity of grain that grows each grain interval
    private int numGrainGrown;
    // interval of grain growth
    private int grainGrowthInterval;
    // random seed value
    private int randomSeed = 0;

    // name of file to record csv
    private String csvFile;

    // maximum grain any patch can hold
    private final int MAX_GRAIN = 50;

    // random number generator
    private Random random;

    private World() {
    }

    /**
     * Read simulation properties from a properties file
     * @param propertiesFile to read properties from
     * @throws IOException when reading properties file
     */
    private void setupProperties(String propertiesFile) throws IOException {
        Properties worldProperties = new Properties();
        // load properties file
        try (FileReader inStream = new FileReader(propertiesFile)) {
            worldProperties.load(inStream);
        }

        // parse properties from properties file
        maxTicks = Integer.parseInt(worldProperties.getProperty("MaxTicks"));
        xPatches = Integer.parseInt(worldProperties.getProperty("XPatches"));
        yPatches = Integer.parseInt(worldProperties.getProperty("YPatches"));
        numPeople = Integer.parseInt(worldProperties.getProperty("NumPeople"));
        maxVision = Integer.parseInt(worldProperties.getProperty("MaxVision"));
        metabolismMax = Integer.parseInt(worldProperties.getProperty( "MetabolismMax"));
        lifeExpectancyMin = Integer.parseInt(worldProperties.getProperty( "LifeExpectancyMin"));
        lifeExpectancyMax = Integer.parseInt(worldProperties.getProperty( "LifeExpectancyMax"));
        percentBestLand = Integer.parseInt(worldProperties.getProperty( "PercentBestLand"));
        numGrainGrown = Integer.parseInt(worldProperties.getProperty( "NumGrainGrown"));
        grainGrowthInterval = Integer.parseInt(worldProperties.getProperty( "GrainGrowthInterval"));

        // initialise random number generator
        random = new Random(randomSeed);

        // determine output csv filename
        File propsFile = new File(propertiesFile);
        String basename = propsFile.getName().split("\\.")[0];
        csvFile = basename + "-seed-" + randomSeed + ".csv";
        System.out.println("Output csv: " + csvFile);
    }

    // get singleton instance of World
    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    public static void main(String[] args) {
        // create and setup world
        World world = World.getInstance();

        // setup properties and random seed
        // check if properties file passed as command-line argument
        if (args.length >= 1) {
            world.setPropertiesFile(args[0]);
        }
        // check if random seed passed as command-line argument
        if (args.length >= 2) {
            world.setRandomSeed(Integer.parseInt(args[1]));
        }

        world.setup();

        // run model
        System.out.println("Running simulation");
        for (int i = 0; i < maxTicks; i++) {
            System.out.println("Tick " + i);
            try {
                world.go();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
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
        for (int x = 0; x < xPatches; x++) {
            for (int y = 0; y < yPatches; y++) {
                System.out.print(getPatch(x,y).grainString());
            }
            System.out.println();
        }
    }

    public void setup() {
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
        patches = new Patch[xPatches][yPatches];

        // initialise patches
        List<Patch> maxGrainPatches = new ArrayList<>();
        for (int x = 0; x < xPatches; x++) {
            patches[x] = new Patch[yPatches];
            for (int y = 0; y < yPatches; y++) {
                // determine how much grain to seed patch with
                int patchGrain = determinePatchGrain();
                // create the new patch
                patches[x][y] = new Patch(x, y, patchGrain, numGrainGrown);
                // store patches with max grain for convenience later
                if (patchGrain != 0) {
                    maxGrainPatches.add(patches[x][y]);
                }
            }
        }

        // spread grain around.  put some back into best land (diffuse)
        // diffuse 5 times
        //printGrain();
        for (int i = 0; i < 5; i++) {
            for (Patch p: maxGrainPatches) {
                // reset to initial grain value
                p.setGrainHere(p.getMaxGrainHere());
                diffuseGrain(p, 0.25f);
            }
            System.out.println("Diffusion 1." + (i+1));
            //printGrain();
        }

        // diffuse 10 times across all patches
        for (int i = 0; i < 10; i++) {
            for (int x = 0; x < xPatches; x++) {
                for (int y = 0; y < yPatches; y++) {
                    diffuseGrain(getPatch(x,y), 0.25f);
                }
            }
            System.out.println("Diffusion 2." + (i+1));
            //printGrain();
        }

        // update max grain
        for (int x = 0; x < xPatches; x++) {
            for (int y = 0; y < yPatches; y++) {
                Patch p = getPatch(x,y);
                p.setMaxGrainHere(p.getGrainHere());
            }
        }
        System.out.println("Reset max grain:");
        //printGrain();
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
     * Set up the initial values for the turtle variables
     */
    private void setupTurtles() {
       for (int i = 0; i < numPeople; i++) {
           // determine location of new turtle
           int x = random.nextInt(xPatches);
           int y = random.nextInt(yPatches);
           // create new turtle and add to the list of turtles
           Turtle turtle = new Turtle(x, y);
           turtles.add(turtle);
       }
    }

    /**
     * Determine the turtles on each patch
     * @param x x coordinate of patch
     * @param y y coordinate of patch
     * @return list of turtles on specified patch
     */
    public List<Turtle> getTurtlesOnPatch(int x, int y) {
        List<Turtle> turtleList = new ArrayList<>();

        for (Turtle turtle: turtles) {
            // check if turtle is on the patch
            if (turtle.getX() == x && turtle.getY() == y) {
                turtleList.add(turtle);
            } 
        }

        return turtleList;
    }

    /**
     * Run one step of the simulation
     */
    private void go() throws Exception {
        // make turtles turn towards grain
        for (Turtle t: turtles) {
            t.turnTowardsGrain();
        }

        // harvest grain
        for (int x = 0; x < xPatches; x++) {
            for (int y = 0; y < yPatches; y++) {
                patches[x][y].harvest();
            }
        }

        // run each turtle
        for (Turtle t: turtles) {
            t.moveEatAgeDie();
        }

        // grow grain on patches if necessary
        if (tick % grainGrowthInterval == 0) {
            for (int x = 0; x < xPatches; x++) {
                for (int y = 0; y < yPatches; y++) {
                    patches[x][y].growGrain();
                }
            }
        }

        // update statistics for each run
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
        float currentGini = computeGini(lorenz);
        System.out.println("Tick: " + tick + ", Gini index: " + currentGini);
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

    /**
     * Compute the Gini index from a set of Lorenz points
     * @param lorenz list of values comprising the lorenz curve
     * @return Gini index corresponding to lorenz curve
     */
    private float computeGini(List<Float> lorenz) {
        float giniIndex = 0;
        float numLorenz = (float)lorenz.size();
        for (int i = 0; i < lorenz.size(); i++) {
            giniIndex += (i+1)/numLorenz - lorenz.get(i);
        }
        return giniIndex;
    }

    /**
     * Determine how much grain each patch should be seeded with
     */
    private int determinePatchGrain() {
        int patchGrain = 0;
        // check if this is best land
        if (random.nextFloat() <= (percentBestLand /100.0)) {
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
        int wrappedX = wrap(x, xPatches);
        int wrappedY = wrap(y, yPatches);
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

    public int getMaxVision() {
        return maxVision;
    }

    public int getMetabolismMax() {
        return metabolismMax;
    }

    public int getLifeExpectancyMin() {
        return lifeExpectancyMin;
    }

    public int getLifeExpectancyMax() {
        return lifeExpectancyMax;
    }

    /**
     * Get patches a particular distance from centre patch in a particular
     * heading.  Note: result is unsorted by distance.
     * @param centreX x coordinate of centre patch
     * @param centreY y coordinate of centre patch
     * @param heading direction to list patches
     * @param distance number of patches to list in direction
     * @return list of patches in a direction up to distance from  centre point
     */
    public List<Patch> getHeadingPatches(int centreX, int centreY, Heading heading, int distance) {
        // list to maintain the patches immediate neighbours
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
            xMax = centreX;
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
        // iterate over 9 cells surrounding and including centre patch
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
        newCentreX = wrap(newCentreX, xPatches);
        newCentreY = wrap(newCentreY, yPatches);

        // return corresponding point
        return new Point(newCentreX, newCentreY);
    }

    private void setRandomSeed(int randomSeed) {
        this.randomSeed = randomSeed;
    }

    private void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }
}