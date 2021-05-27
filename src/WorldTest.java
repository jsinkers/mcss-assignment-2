/*
 * SWEN90004 Assignment 2 - Wealth Distribution
 * James Sinclair - 1114278, Yujun Yan - 952112, Junkai Xing - 1041973
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for World
 */
class WorldTest {
    World world;

    @BeforeEach
    void setUp() {
        world = World.getInstance();
        world.setup();
    }

    /**
     * test World.getHeadingPatches produces 5 neighbours in
     * middle of map, heading North, with distance of 5
     */
    @Test
    void getHeadingPatches() {
        List<Patch> patches = world.getHeadingPatches(10, 27, Heading.NORTH, 5);
        assertEquals(5, patches.size());
    }

    /**
     * test World.getHeadingPatches produces 3 neighbours on right edge of map
     * heading east
     */
    @Test
    void getHeadingPatches2() {
        List<Patch> patches = world.getHeadingPatches(51, 27, Heading.EAST, 3);
        assertEquals(3, patches.size());
    }

    /**
     * test World.getHeadingPatches produces 2 neighbours in middle of map
     * heading south
     */
    @Test
    void getHeadingPatches3() {
        List<Patch> patches = world.getHeadingPatches(10, 27, Heading.SOUTH, 2);
        assertEquals(2, patches.size());
    }

    /**
     * test World.getHeadingPatches produces 2 neighbours near left edge
     * heading west
     */
    @Test
    void getHeadingPatches4() {
        List<Patch> patches = world.getHeadingPatches(2, 0, Heading.WEST, 2);
        assertEquals(2, patches.size());
    }

    /**
     * test World.getHeadingPatches produces 0 neighbours near left edge
     * heading west
     */
    @Test
    void getHeadingPatches5() {
        List<Patch> patches = world.getHeadingPatches(2, 0, Heading.WEST, 0);
        assertEquals(0, patches.size());
    }

    /**
     * test World.getHeadingPatches produces 3 neighbours on bottom edge
     * heading south
     */
    @Test
    void getHeadingPatches6() {
        List<Patch> patches = world.getHeadingPatches(2, 0, Heading.SOUTH, 3);
        assertEquals(3, patches.size());
    }

    /**
     * test World.getPatchNeighbours finds 8 surrounding neighbours at the
     * bottom-left corner
     */
    @Test
    void getPatchNeighbours() {
        List<Patch> neighbours = world.getPatchNeighbours(0,0);
        // the patch should have 8 neighbours
        assertEquals(neighbours.size(), 8);
    }

    /**
     * test world.getNextPatch finds (0,1) heading North from (0,0)
     */
    @Test
    void getNextPatch() {
        Point p1 = world.getNextPatch(0,0, Heading.NORTH);
        assertEquals(0, p1.getX());
        assertEquals(1, p1.getY());
    }

    /**
     * test world.getNextPatch finds (0,0) heading North from (0,50)
     */
    @Test
    void getNextPatch2() {
        Point p1 = world.getNextPatch(0,50, Heading.NORTH);
        assertEquals(0, p1.getX());
        assertEquals(0, p1.getY());
    }

    /**
     * test world.getNextPatch finds (50,0) heading West from (0,0)
     */
    @Test
    void getNextPatch3() {
        Point p1 = world.getNextPatch(0,0, Heading.WEST);
        assertEquals(50, p1.getX());
        assertEquals(0, p1.getY());
    }

    /**
     * test world.getNextPatch finds (0,50) heading South from (0,0)
     */
    @Test
    void getNextPatch4() {
        Point p1 = world.getNextPatch(0,0, Heading.SOUTH);
        assertEquals(0, p1.getX());
        assertEquals(50, p1.getY());
    }

    /**
     * test world.getNextPatch finds (1,0) heading East from (0,0)
     */
    @Test
    void getNextPatch5() {
        Point p1 = world.getNextPatch(0,0, Heading.EAST);
        assertEquals(1, p1.getX());
        assertEquals(0, p1.getY());
    }

    /**
     * normal patch: test world.getPatch wrapping returns (0,0) from (0,0)
     */
    @Test
    void getPatch() {
        // normal patch
        Patch p1 = world.getPatch(0,0);
        assertEquals(0, p1.X);
        assertEquals(0, p1.Y);
    }

    /**
     * off left patch: test world.getPatch returns (50,5) from (-1,5)
     */
    @Test
    void getPatch2() {
        // off left patch
        Patch p1 = world.getPatch(-1,5);
        assertEquals(50, p1.X);
        assertEquals(5, p1.Y);
    }

    /**
     * off top patch: test world.getPatch returns (50,5) from (-1,5)
     */
    @Test
    void getPatch3() {
        // off top patch
        Patch p1 = world.getPatch(0,61);
        assertEquals(0, p1.X);
        assertEquals(10, p1.Y);
    }

    /**
     * test World.diffuseGrain produces expected grain diffusion
     */
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

        // this should distribute 25% of 50 grain amongst 8 neighbours
        // (50*0.25/8) = 1.5625 grain distributed to each neighbour.
        // central patch should be left with 42 grain

        // check centre patch is left with correct amount of grain
        assertEquals(37.5f, centrePatch.getGrainHere());
        for (Patch p: patches) {
            assertEquals(1.5625f, p.getGrainHere());
        }
    }

    /**
     * Check Lorenz is computed correctly
     */
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

    /**
     * Check World.computeGini computes Gini index correctly on simple test case
     */
    @Test
    void computeGini() {
        // test case: 2 turtles, with wealth 62 and 16.  gini-index: 0.29487
        List<Integer> wealth = new ArrayList<>();
        wealth.add(62);
        wealth.add(16);
        List<Float> lor = world.computeLorenz(wealth);
        float gini = world.computeGini(lor);
        assertEquals(0.29487, gini, 0.001);
    }
}