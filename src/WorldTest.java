import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldTest {
    World world;

    @BeforeEach
    void setUp() {
        world = World.getInstance();
        world.setup();
    }

    @Test
    void getHeadingPatches() {

    }

    @Test
    void getPatchNeighbours() {
        List<Patch> neighbours = world.getPatchNeighbours(0,0);
        // the patch should have 8 neighbours
        assertEquals(neighbours.size(), 8);
    }

    @Test
    void getNextPatch() {
        Point p1 = world.getNextPatch(0,0, Heading.NORTH);
        assertEquals(0, p1.getX());
        assertEquals(1, p1.getY());
    }

    @Test
    void getNextPatch2() {
        Point p1 = world.getNextPatch(0,50, Heading.NORTH);
        assertEquals(0, p1.getX());
        assertEquals(0, p1.getY());
    }

    @Test
    void getNextPatch3() {
        Point p1 = world.getNextPatch(0,0, Heading.WEST);
        assertEquals(50, p1.getX());
        assertEquals(0, p1.getY());
    }

    @Test
    void getNextPatch4() {
        Point p1 = world.getNextPatch(0,0, Heading.SOUTH);
        assertEquals(0, p1.getX());
        assertEquals(50, p1.getY());
    }

    @Test
    void getNextPatch5() {
        Point p1 = world.getNextPatch(0,0, Heading.EAST);
        assertEquals(1, p1.getX());
        assertEquals(0, p1.getY());
    }

    @Test
    void getPatch() {
        // normal patch
        Patch p1 = world.getPatch(0,0);
        assertEquals(0, p1.X);
        assertEquals(0, p1.Y);
    }

    @Test
    void getPatch2() {
        // off left patch
        Patch p1 = world.getPatch(-1,5);
        assertEquals(50, p1.X);
        assertEquals(5, p1.Y);
    }
    @Test
    void getPatch3() {
        // off top patch
        Patch p1 = world.getPatch(0,61);
        assertEquals(0, p1.X);
        assertEquals(10, p1.Y);
    }

    @Test
    void diffuseGrain() {
        // set up neighbours of (1,1) with no grain
        List<Patch> patches = world.getPatchNeighbours(1,1);
        for (Patch p: patches) {
            p.setGrainHere(0);
        }

        // set (1,1) to have 50 grain
        Patch centrePatch = world.getPatch(1,1);
        centrePatch.setGrainHere(50);

        // diffuse grain from centrePatch, 0.25f
        world.diffuseGrain(centrePatch, 0.25f);

        // this should distribute 25% of 50 grain amongst 8 neighbours, rounded
        // down. i.e. floor(50*0.25/8) = 1 grain distributed to each neighbour.
        // central patch should be left with 42 grain

        // check centre patch is left with correct amount of grain
        assertEquals(42, centrePatch.getGrainHere());
        for (Patch p: patches) {
            assertEquals(1, p.getGrainHere());
        }
    }

    @Test
    void computeLorenz() {
        // test case: 2 turtles, with wealth 62 and 16.  gini-index: 0.29487
        List<Integer> wealth = new ArrayList<>();
        wealth.add(62);
        wealth.add(16);
        List<Float> lor = world.computeLorenz(wealth);
        // 16/78
        assertEquals(0.2051, lor.get(0), 0.001);
        // 78/78
        assertEquals(1, lor.get(1), 0.001);
    }

    @Test
    void updateGini() {
        // test case: 2 turtles, with wealth 62 and 16.  gini-index: 0.29487
        List<Integer> wealth = new ArrayList<>();
        wealth.add(62);
        wealth.add(16);
        List<Float> lor = world.computeLorenz(wealth);
        float gini = world.updateGini(lor);
        assertEquals(0.29487, gini, 0.001);
    }
}