/**
 * Represents a turtle able to move through the world, harvesting grain
 */
public class Turtle {
    private int age;
    private int wealth;
    private int x;
    private int y;

    private final int lifeExpectancy;
    private final int metabolism;
    private final int vision;

    public Turtle(int lifeExpectancy, int metabolism, int vision, int x, int y) {
        this.lifeExpectancy = lifeExpectancy;
        this.metabolism = metabolism;
        this.vision = vision;
        this.x = x;
        this.y = y;
    }

    private void turnTowardsGrain() {

    }

    private void grainAhead() {

    }

    public void moveEatAgeDie() {

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
}
