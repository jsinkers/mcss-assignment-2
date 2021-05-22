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

    public Turtle() {
        random = World.getInstance().getRandom();
        setInitialTurtleVars();
    }


    /**
     * determine the direction which is most profitable for each turtle in
     * the surrounding patches within the turtles' vision
     */
    private void turnTowardsGrain() throws Exception {
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
        List<Patch> headingPatches = World.getInstance().getHeadingPatches(x,y,heading,vision);
        //check if the returned heading patches list has the correct leangth
        if(vision<headingPatches.size()){
            throw new Exception("the number of heading patches does not match the turtle's vision");
        }
        //add up the total grain in the heading patches that can be seen by the turtle
        for(int i=0;i<headingPatches.size();i++){
            total=total+headingPatches.get(i).getGrainHere();
            howFar=howFar+1;
        }
        return total;
    }

    public void moveEatAgeDie() throws Exception {
        //the turtle move forward by one distance
        List<Patch> headingPatches = World.getInstance().getHeadingPatches(x,y,heading,vision);
        //get the next patch the turtle will move to
        //TODO: need to make sure that headPatches.get(0) get the first patch in the heading direction
        if(headingPatches.size()>0) {
            Patch nextPatch = headingPatches.get(0);
            //update the turtle position to the next patch
            x=nextPatch.X;
            y=nextPatch.Y;
        }else {
            throw new Exception("there is no patch this turtle can move to");
        }

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
        //TODO: double check if we need to update the position of a turtle when it is born,
        // and how do we know the position of a turtle
        //initialize the random direction the turtle head to
        heading= Heading.values()[random.nextInt(Heading.values().length)];
        //set a random life expectancy for the turtle
        lifeExpectancy = World.getInstance().getLIFE_EXPECTANCY_MIN()
                +random.nextInt(World.getInstance().getLIFE_EXPECTANCY_MAX()
                -World.getInstance().getLIFE_EXPECTANCY_MIN()+1);

        metabolism=1+random.nextInt(World.getInstance().getMETABOLISM_MAX());
        wealth=metabolism+random.nextInt(50);
        vision=1+random.nextInt(World.getInstance().getMAX_VISION());

    }


    /**
     * This is the extension that implements the wealth inheritance mechanism.
     * Used to reset a turtle's properties when it dies.
     * Also used to initialize a turtle's properties when it is born.
     * Only difference is that a offspring has the same wealth as the parent
     */
    public void setInitialTurtleVarsExtension(){
        //initialize the age to be 0
        age=0;
        //TODO: double check if we need to update the position of a turtle when it is born,
        // and how do we know the position of a turtle
        //initialize the random direction the turtle head to
        heading= Heading.values()[random.nextInt(Heading.values().length)];
        //set a random life expectancy for the turtle
        lifeExpectancy = World.getInstance().getLIFE_EXPECTANCY_MIN()
                +random.nextInt(World.getInstance().getLIFE_EXPECTANCY_MAX()
                -World.getInstance().getLIFE_EXPECTANCY_MIN()+1);

        metabolism=1+random.nextInt(World.getInstance().getMETABOLISM_MAX());
        //a offspring has the same wealth as the parent
        //TODO: maybe we can remove this line as it is redundant.
        // The only reason it is here is that we need to use this
        // to represent wealth is inherited
        wealth=wealth;
        vision=1+random.nextInt(World.getInstance().getMAX_VISION());

    }

    public int getWealth() {
        return wealth;
    }
}
