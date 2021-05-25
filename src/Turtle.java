import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Represents a turtle able to move through the world, harvesting grain
 */
public class Turtle {
    private int age;
    private int wealth;

    private int x;  //the current turtle position in x-axis
    private int y;   //the current turtle position in y-axis

    private int lifeExpectancy;
    private int metabolism;
    private int vision;
    private final Random random;
    //direction of where the turtle is heading (degree)
    private Heading heading;
    private final World world;

    // whether wealth should be inherited by turtles
    private boolean inheritance = false;

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
    }


    /**
     * determine the direction which is most profitable for each turtle in
     * the surrounding patches within the turtles' vision
     */
    public void turnTowardsGrain() throws Exception {
        //start from checking the amount of grain available in the West side
         heading=Heading.WEST;
        Heading bestDirection = Heading.WEST;
        int bestAmount = grainAhead();
        //try another direction (in this case 90 degree) to
        //check if the turtle can harvest more grain
        heading = Heading.NORTH;
        if (grainAhead()>bestAmount){
            bestDirection=Heading.NORTH;
            bestAmount=grainAhead();
        }
        //try another direction 180 degree, repeat the above process
        heading=Heading.EAST;
        if (grainAhead()>bestAmount){
            bestDirection=Heading.EAST;
            bestAmount=grainAhead();
        }
        //try another direction 270 degree, repeat the above process
        heading = Heading.SOUTH;
        if (grainAhead()>bestAmount){
            bestDirection=Heading.SOUTH;
            //TODO: check where "beastAmount" is used,
            // maybe we should put harvest() into the turtle class
            bestAmount=grainAhead();
        }
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
        //how far the patch is ahead of a turtle, the initial heading patch is 1 distance ahead
        int howFar= 1;
        //TODO: need to double check if this method is used correctly
        //get a list of patches which can be seen by the turtle in its heading direction
        //the number of the patches in the list depends on the vision of the turtle
        List<Patch> headingPatches = world
                .getHeadingPatches(x, y, heading, vision);
        //check if the returned heading patches list has the correct leangth
        if (vision != headingPatches.size()){
            throw new Exception(String.format("Location (%d,%d), %s: the" +
                    " number " +
                "of heading patches, %d, does not match the " +
                "turtle's vision, %d", x, y, heading, headingPatches.size(),
                    vision));
        }
        //add up the total grain in the heading patches that can be seen by the turtle
        for(int i=0;i<headingPatches.size();i++){
            total=total+headingPatches.get(i).getGrainHere();
            howFar=howFar+1;
        }
        return total;
    }

    public void moveEatAgeDie() throws Exception {
        //update the heading direction of the turtle
        //TODO: check this works properly
        Point nextPatch = world.getNextPatch(x,y,heading);
        //update the turtle position to the new position after moving one distance
        x= (int) nextPatch.getX();
        y= (int) nextPatch.getY();

        //consume some grain according to metabolism
        wealth=wealth-metabolism;
        //grow older
        age++;
        //check for death conditions: if you have no grain or
        //you're older than the life expectancy or if some random factor
        //holds, then you "die" and are "reborn" (in fact, your variables
        //are just reset to new random values)
        if(wealth<0 || age>=lifeExpectancy){
            //TODO: need to understand how a turtle is initialized
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
        // set a random life expectancy for the turtle
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
            wealth = metabolism + random.nextInt(world.getMaxGrain());
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
        lifeExpectancy = world.getLifeExpectancyMin() +
            random.nextInt(world.getLifeExpectancyMax() - world.getLifeExpectancyMin() + 1);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    @Override
    public String toString() {
        return "Turtle{" + "wealth=" + wealth + ", x=" + x + ", y=" + y + '}';
    }
}
