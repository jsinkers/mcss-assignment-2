import java.util.ArrayList;

/**
 * Singleton class that represents the World, running the simulation
 */
public class World {
    // singleton instance
    private static World instance;
    // maximum grain any patch can hold
    private final int maxGrain;
    // turtles (agents) in the world
    private final ArrayList<Turtle> turtles = new ArrayList<>();
    // patches (divisions) of the world
    private final ArrayList<Turtle> patches = new ArrayList<>();

    // current value of the lorenz
    private ArrayList<Float> lorenz;

    // current value of the gini coefficient
    private float gini;
    // current tick of the world
    private int tick;

    public World() {
        // read parameters from config file
        maxGrain = 0;
        // setup
    }

    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    public static void main(String[] args) {
        // create world

        // run model

        // write results to csv
    }

    public void setup() {

    }

    private void setupPatches() {

    }

    private void setInitialTurtleVars() {

    }

    private void setupTurtles() {

    }

    private void go() {

    }

    private void updateLorenzAndGini() {

    }
}

