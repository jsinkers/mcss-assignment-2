import java.util.List;

/**
 * Represents a turtle able to move through the world, harvesting grain
 */
public class Turtle {
    private int age;
    private int wealth;
    private int x;  //the current turtle position in x-axis
private int y;   //the current turtle position in y-axis


    private final int lifeExpectancy;
    private final int metabolism;
    private final int vision;
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

    }

    public int getWealth() {
        return wealth;
    }
}
