/*
 * SWEN90004 Assignment 2 - Wealth Distribution
 * James Sinclair - 1114278, Yujun Yan - 952112, Junkai Xing - 1041973
 */

import java.util.List;

/**
 * Represents a discrete patch of the world.  Grain grows on each patch
 * and can be harvested
 */
public class Patch {
    // current amount of grain on the patch
    private double grainHere;
    // amount of grain that grows at a given interval
    private final int numGrainGrown;
    // maximum grain the patch can hold
    private double maxGrainHere;
    // x, y coordinates of the patch
    public final int X;
    public final int Y;

    /**
     * initialize the patch
     *
     * @param x             x coordinates of the patch
     * @param y             y coordinates of the patch
     * @param maxGrainHere  maximum grain the patch can hold
     * @param numGrainGrown amount of grain that grows at a given interval
     */
    public Patch(int x, int y, int maxGrainHere, int numGrainGrown) {
        this.maxGrainHere = maxGrainHere;
        this.grainHere = maxGrainHere;
        this.numGrainGrown = numGrainGrown;
        this.X = x;
        this.Y = y;
    }

    /**
     * Grow grain on the patch
     */
    public void growGrain() {
        // if patch doesn't have maximum grain, add numGrainGrown
        if (grainHere < maxGrainHere) {
            grainHere += numGrainGrown;
            // ensure this doesn't exceed capacity
            if (grainHere > maxGrainHere) {
                grainHere = maxGrainHere;
            }
        }
    }

    /**
     * harvest grain on the patch. If there are more than one turtle on the
     * patch,
     * divide the grain evenly amongst all turtles on the patch. No grain
     * will be left
     * on the patch after harvest, unless there is no turtle on the patch.
     */
    public void harvest() {
        // get all of the turtles on the patch
        List<Turtle> turtleList = World.getInstance().getTurtlesOnPatch(X, Y);

        // if there are no turtles present, there's nothing left to do
        if (turtleList.size() == 0) {
            return;
        }

        // determine amount of grain to share, by dividing it evenly among
        // the turtles on the patch, rounding down
        double grainToShare = getGrainHere() / turtleList.size();

        // have turtles harvest before any turtle sets the patch to 0
        for (Turtle t : turtleList) {
            // update the wealth of each turtle
            int turtleWealth = t.getWealth() + (int) grainToShare;
            t.setWealth(turtleWealth);
        }

        // set Grain to 0 on the patch after harvest
        setGrainHere(0);
    }

    /**
     * @return the amount of grain on the patch
     */
    public double getGrainHere() {
        return grainHere;
    }

    /**
     * @param grainHere the amount of grain on the patch
     */
    public void setGrainHere(double grainHere) {
        this.grainHere = grainHere;
    }

    /**
     * add v gain to the patch
     *
     * @param v the amount of grain to be added to the patch
     */
    public void addGrain(double v) {
        this.grainHere += v;
    }

    /**
     * @return maximum grain the patch can hold
     */
    public double getMaxGrainHere() {
        return maxGrainHere;
    }

    /**
     * @param maxGrainHere maximum grain the patch can hold
     */
    public void setMaxGrainHere(double maxGrainHere) {
        this.maxGrainHere = maxGrainHere;
    }

    @Override
    public String toString() {
        return "P{(" + X + "," + Y + "), grain=" + grainHere + "/" +
                maxGrainHere + '}';
    }

    /**
     * returns a short string of "grainHere/maxGrainHere"
     */
    public String grainString() {
        return String.format("%1$" + 6 + "s", grainHere + "/" + maxGrainHere);
    }
}
