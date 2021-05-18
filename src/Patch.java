public class Patch {

    private int grainHere;
    private final int maxGrainHere;
    private final int numGrainGrown;

    public Patch(int maxGrainHere, int numGrainGrown) {
        this.maxGrainHere = maxGrainHere;
        this.grainHere = maxGrainHere;
        this.numGrainGrown = numGrainGrown;
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

}
