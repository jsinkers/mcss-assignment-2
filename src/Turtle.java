/**
 * Represents a turtle able to move through the world, harvesting grain
 */
public class Turtle {
    private int age;
    private int wealth;

    private final int lifeExpectancy;
    private final int metabolism;
    private final int vision;

    public Turtle(int lifeExpectancy, int metabolism, int vision) {
        this.lifeExpectancy = lifeExpectancy;
        this.metabolism = metabolism;
        this.vision = vision;
    }


    /*
    determine the direction which is most profitable for each turtle in
    the surrounding patches within the turtles' vision

    TODO: need to sync the heading patch mechanism with "World" class
     */
    private void turnTowardsGrain() {
        int heading = 0;
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

    private void grainAhead() {

    }

    public void moveEatAgeDie() {

    }

    public int getWealth() {
        return wealth;
    }
}
