public class Patch {
    private int grainHere;
    private final int maxGrainHere;

    public Patch(int maxGrainHere) {
        this.maxGrainHere = maxGrainHere;
        this.grainHere = maxGrainHere;
    }

    public void growGrain() {

    }

    public void harvest() {

    }

    public int getGrainHere() {
        return grainHere;
    }
}
