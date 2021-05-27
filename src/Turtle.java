import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Represents a turtle able to move through the world, harvesting grain
 */
public class Turtle {
    // When computing new wealth of a turtle, the random component of wealth is
    // bounded by this value
    private final int WEALTH_BOUND = 50;
    // current age of the turtle
    private int age;
    // current wealth of the turtle
    private int wealth;

    //the current turtle x/y positions
    private int x;
    private int y;

    // turtle's life expectancy
    private int lifeExpectancy;
    // turtle's metabolism value: how much grain to consume at each tick
    private int metabolism;
    // turtle's vision: the number of heading patches that can be seen by a turtle
    private int vision;
    // direction of where the turtle is heading
    // (enum Heading {NORTH,EAST,SOUTH,WEST})
    private Heading heading;

    // random number generator
    private final Random random;

    // world running the sim
    private final World world;

    // a flag used to determine whether wealth should be inherited by turtles
    private final boolean inheritance;

    /**
     * Constructor used to initialize a turtle
     * @param x initial x coordinate of the turtle position
     * @param y initial y coordinate of the turtle position
     */
    public Turtle(int x, int y) {
        //initialize the turtle position with the coordinate information passed from the caller
        this.x=x;
        this.y=y;
        this.world = World.getInstance();
        //initialize other turtle properties
        random = world.getRandom();
        inheritance = world.getInheritance();

        setInitialTurtleVars();
        age = random.nextInt(lifeExpectancy);
    }

    /**
     * determine the direction which is most profitable (can harvest the most grains) for each turtle in
     * the surrounding patches within the turtles' vision
     */
    public void turnTowardsGrain() throws Exception {
        // highest amount of grain
        int bestAmount = 0;
        // best direction to head
        Heading bestDirection = Heading.NORTH;
        // check each direction, finding the direction with the most grain ahead
        for (Heading h: Heading.values()) {
            heading = h;
            int grain = grainAhead();
            if (grain > bestAmount) {
                bestAmount = grain;
                bestDirection = h;
            }
        }
        // change heading to the best direction
        heading=bestDirection;
    }

    /**
     * calculate the total number of grain in the heading patches that can be seen by the turtle
     *
     * @return the total number of grain in the heading patches that can be seen by the turtle
     */
    private int grainAhead() throws Exception {
        //the total grain in the heading patches that can be seen by the turtle
        int total = 0;
        //get a list of patches which can be seen by the turtle in its heading direction
        //the number of the patches in the list depends on the vision of the turtle
        List<Patch> headingPatches = world
                .getHeadingPatches(x, y, heading, vision);
        //check if the returned heading patches list has the correct size (size == Turtle's vision),
        // raise an exception if this is not the case
        if (vision != headingPatches.size()){
            throw new Exception(String.format("Location (%d,%d), %s: the" +
                    " number " +
                "of heading patches, %d, does not match the " +
                "turtle's vision, %d", x, y, heading, headingPatches.size(),
                    vision));
        }
        //add up the total grain in the heading patches that can be seen by the turtle
        for (Patch headingPatch : headingPatches) {
            total = total + (int)headingPatch.getGrainHere();
        }
        return total;
    }

    /**
     * Simulate a turtle's life cycle. After a turtle harvests the grain on its patch,
     * this method is called to make the turtle move forward 1 position in its heading direction.
     * Then consume some grain according to metabolism and grow older. If it reaches its life expectancy
     * or has grain<0, it will "die" and "reborn".
     */
    public void moveEatAgeDie() {
        //update the turtle position to the new position after moving one distance
        Point nextPatch = world.getNextPatch(x,y,heading);
        x= (int) nextPatch.getX();
        y= (int) nextPatch.getY();

        //consume some grain according to metabolism
        wealth=wealth-metabolism;
        //grow older
        age++;
        // check for death conditions: if you have no grain or
        // you're older than the life expectancy or if some random factor
        // holds, then you "die" and are "reborn" (in fact, your variables
        // are just reset to new random values)
        if(wealth<0 || age >= lifeExpectancy) {
            setInitialTurtleVars();
        }

    }

    /**
     * Used to reset a turtle's properties when it dies.
     * Also used to initialize a turtle's properties when it is born
     */
    public void setInitialTurtleVars(){
        //initialize the age to be 0
        age=0;
        // initialize the random direction the turtle head to
        resetHeading();
        // set life expectancy, metabolism, wealth, vision
        resetLifeExpectancy();
        resetMetabolism();
        resetWealth();
        resetVision();
    }

    private void resetHeading() {
        heading = Heading.values()[random.nextInt(Heading.values().length)];
    }

    /**
     * This is the extension that implements the wealth inheritance mechanism.
     * Used to reset a turtle's wealth when it dies.
     * Also used to initialize a turtle's wealth when it is born.
     * Only difference is that the offspring has the same wealth as the parent
     */
    private void resetWealth() {
        // choose a new wealth value. if inheritance is in play an offspring
        // has the same wealth as the parent, and if this is the first tick,
        // set the wealth randomly
        if (!inheritance || world.getTick() == 0) {
            // random wealth
            wealth = metabolism + random.nextInt(WEALTH_BOUND);
        } else if (inheritance) {
            // if wealth is negative, inherit nothing from parent, otherwise
            // retain the parent's wealth
            wealth = metabolism + Math.max(0, wealth);
        }
    }

    /**
     * Choose random vision for the turtle
     */
    private void resetVision() {
        vision = 1 + random.nextInt(world.getMaxVision());
    }

    /**
     * Choose random metabolism for the turtle
     */
    private void resetMetabolism() {
        metabolism = 1 + random.nextInt(world.getMetabolismMax());
    }

    /**
     * Determine new life expectancy for turtle
     */
    private void resetLifeExpectancy() {
        int minLifeExp = world.getLifeExpectancyMin();
        int maxLifeExp = world.getLifeExpectancyMax();
        lifeExpectancy = minLifeExp +
                random.nextInt(maxLifeExp - minLifeExp + 1);
    }

    /**
     * @return the x coordination value of the turtle position
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y coordination value of the turtle position
     */
    public int getY() {
        return y;
    }

    /**
     * @return the turtle's wealth
     */
    public int getWealth() {
        return wealth;
    }

    /**
     * @param wealth the turtle's wealth you wish to set to
     */
    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    @Override
    public String toString() {
        return "Turtle{" + "wealth=" + wealth + ", x=" + x + ", y=" + y + '}';
    }
}
