import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
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
}