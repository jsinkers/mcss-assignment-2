import java.util.List;

/**
 * Represents a discrete patch of the world.  Grain grows on each patch
 * and can be harvested
 */
public class Patch {
    // current amount of grain on the patch
    private int grainHere;
    // amount of grain that grows at a given interval
    private final int numGrainGrown;
    // maximum grain the patch can hold
    private int maxGrainHere;
    // x, y coordinates of the patch
    public final int X;
    public final int Y;

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
     * Divide the grain evenly amongst all turtles on the patch
     */
    public void harvest() {
        // get all of the turtles on the patch
        List<Turtle> turtleList = World.getInstance().getTurtlesOnPatch(X, Y);
        // determine amount of grain to share, by dividing it evenly among
        // the turtles on the patch, rounding down
        int grainToShare = getGrainHere() / turtleList.size();

        // have turtles harvest before any turtle sets the patch to 0
        for (Turtle t: turtleList) {
            // update the wealth of each turtle
            int turtleWealth = t.getWealth() + grainToShare;
            t.setWealth(turtleWealth);
        }

        // set Grain to 0 on the patch after harvest
        setGrainHere(0);
    }

    public int getGrainHere() {
        return grainHere;
    }

    public void setGrainHere(int grainHere) {
        this.grainHere = grainHere;
    }

    public void addGrain(int v) {
        this.grainHere += v;
    }

    public int getMaxGrainHere() {
        return maxGrainHere;
    }

    public void setMaxGrainHere(int maxGrainHere) {
        this.maxGrainHere = maxGrainHere;
    }

    @Override
    public String toString() {
        return "P{(" + X + "," + Y + "), grain=" + grainHere + "/" + maxGrainHere + '}';
    }

    /**
     * returns a short string of "grainHere/maxGrainHere"
     */
    public String grainString() {
        return String.format("%1$"+6+"s", grainHere + "/" + maxGrainHere);
    }
}
