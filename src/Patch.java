public class Patch {

    private int grainHere;
    private final int numGrainGrown;
    private int maxGrainHere;
    public final int X;
    public final int Y;

    public Patch(int x, int y, int maxGrainHere, int numGrainGrown) {
        this.maxGrainHere = maxGrainHere;
        this.grainHere = maxGrainHere;
        this.numGrainGrown = numGrainGrown;
        this.X = x;
        this.Y = y;
    }

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
        return "P{" +
                "(" + X +
                "," + Y +
                "), grain=" + grainHere +
                "/" + maxGrainHere +
                '}';
    }

    public String grainString() {
        return String.format("%1$"+7+"s", grainHere + "/" + maxGrainHere);
    }
}
