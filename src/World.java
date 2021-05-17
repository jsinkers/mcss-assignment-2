import java.util.ArrayList;

/**
 * Singleton class that represents the World, running the simulation
 */
public class World {
    // singleton instance
    private static World instance;
    // maximum grain any patch can hold
    private final int MAX_GRAIN;
    // number of iterations to run simulation for
    private static final int MAX_ITERATIONS = 100;
    // turtles (agents) in the world
    private final ArrayList<Turtle> turtles = new ArrayList<>();
    // patches (divisions) of the world
    private final ArrayList<Turtle> patches = new ArrayList<>();
    // current values of the lorenz curve
    private ArrayList<Float> lorenz;
    // current value of the gini coefficient
    private float gini;
    // current tick of the world
    private int tick;

    public World() {
        // read parameters from config file
        MAX_GRAIN = 0;
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
        world.setup();

        // run model
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            world.go();
            // update statistics
            world.updateLorenzAndGini();
        }

        // write results to csv

    }

    public void setup() {
        setupPatches();
        setupTurtles();
    }

    private void setupPatches() {

    }

    private void setInitialTurtleVars() {

    }

    private void setupTurtles() {

    }

    private void go() {
        // run each patch

        // run each turtle
    }

    private void updateLorenzAndGini() {

    }
}

