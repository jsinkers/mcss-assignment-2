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
     * TODO: not sure if this method belong to Turtle or the Patch, maybe it should beong to the turtle since turtle do the Harvest action
     */
    public void harvest() {

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
