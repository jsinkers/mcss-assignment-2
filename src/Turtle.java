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



    private final int[] allDirection = {0,90,180,270};  //directions a turtle can head to
    private int lifeExpectancy;
    private int metabolism;
    private int vision;
    //direction of where the turtle is heading (degree)
    //TODO: check if we still decide to use degree to represent the direction
    private int heading;

    public Turtle(int lifeExpectancy, int metabolism, int vision) {
        this.lifeExpectancy = lifeExpectancy;
        this.metabolism = metabolism;
        this.vision = vision;
    }


    /**
     * determine the direction which is most profitable for each turtle in
     * the surrounding patches within the turtles' vision
     * TODO: need to sync the heading patch mechanism with "World" class
     */
    private void turnTowardsGrain() {
         heading = 0;
        int bestDirection = 0;
        int bestAmount = grainAhead();
        //try another direction (in this case 90 degree) to
        //check if the turtle can harvest more grain
        heading = 90;
        if (grainAhead()>bestAmount){
            bestDirection=90;
            bestAmount=grainAhead();
        }
        //try another direction 180 degree, repeat the above process
        heading=180;
        if (grainAhead()>bestAmount){
            bestDirection=180;
            bestAmount=grainAhead();
        }
        //try another direction 270 degree, repeat the above process
        heading = 270;
        if (grainAhead()>bestAmount){
            bestDirection=270;
            bestAmount=grainAhead();
        }
        heading=bestDirection;
    }

    /**
     * calculate the total number of grain in the heading patches that can be seen by the turtle
     *
     * @return the total number of grain in the heading patches that can be seen by the turtle
     */
    private int grainAhead() {
        //the total grain in the heading patches that can be seen by the turtle
        int total = 0;
        //how far the patch is ahead of a turtle, the initial heading patch is 1 distance ahead
        int howFar= 1;
        //TODO: need to double check if this method is used correctly
        //get a list of patches which can be seen by the turtle in its heading direction
        //the number of the patches in the list depends on the vision of the turtle
        List<Patch> headingPatches = World.getInstance().getHeadingPatches(x,y,heading,vision);
        //add up the total grain in the heading patches that can be seen by the turtle
        for(int i=0;i<vision;i++){
            total=total+headingPatches.get(i).getGrainHere();
            howFar++;
        }
        return total;
    }

    public void moveEatAgeDie() {
        //the turtle move forward by one distance
        List<Patch> headingPatches = World.getInstance().getHeadingPatches(x,y,heading,vision);
        //get the next patch the turtle will move to
        Patch nextPatch=headingPatches.get(0);
        //update the turtle position to the next patch
        x=nextPatch.X;
        y=nextPatch.Y;
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
        }

    }

    /**
     * Used to reset a turtle's properties when it dies.
     * Also used to initialize a turtle's properties when it is born
     */
    public void setInitialTurtleVars(){
        //initialize the age to be 0
        age=0;
        //TODO: double check if we need to update the position of a turtle when it is born
        //initialize the random direction the turtle head to
        int rnd = new Random().nextInt(allDirection.length);
        heading= allDirection[rnd];
        //set a random life expectancy for the turtle
        lifeExpectancy = World.getInstance().getLIFE_EXPECTANCY_MIN()
                +new Random().nextInt(World.getInstance().getLIFE_EXPECTANCY_MAX()
                -World.getInstance().getLIFE_EXPECTANCY_MIN()+1);

        metabolism=1+new Random().nextInt(World.getInstance().getMETABOLISM_MAX());
        wealth=metabolism+new Random().nextInt(50);
        vision=1+new Random().nextInt(World.getInstance().getMAX_VISION());

    }

    public int getWealth() {
        return wealth;
    }
}
