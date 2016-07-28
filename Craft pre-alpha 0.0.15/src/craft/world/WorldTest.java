package craft.world;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WorldTest {
	public static World world = new World();
	static {
		world.createEmptyMap(128, 128, 128);
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testGetBlock() {
		assertEquals(0, world.getBlock(0, 0, 0));
		assertEquals(0, world.getBlock(4, 5, 0));
		assertEquals(0, world.getBlock(-8, 8, 53));
		assertEquals(0, world.getBlock(-35, 8, 53));
	}

	@Test(timeout=1000)
	public void testSetBlock() {
		
	}

	@Test()
	public void testSetBlockDirectly() {
		world.setBlockDirectly(0, 0, 0, 5);
		world.setBlockDirectly(-32, 7, 45, 14);
		world.setBlockDirectly(-14, 75, 29, 7);
		world.setBlockDirectly(-64, 114, 31, 6);
		
		world.setBlockDirectly(-63, 127, 31, 32);
		world.setBlockDirectly(-2, 17, 38, 37);
		world.setBlockDirectly(-63, 100, -63, 18);
		world.setBlockDirectly(60, 76, 63, 32);
		
		assertEquals(5, world.getBlock(0, 0, 0));
		assertEquals(14, world.getBlock(-32, 7, 45));
		assertEquals(7, world.getBlock(-14, 75, 29));
		assertEquals(6, world.getBlock(-64, 114, 31));
		
		assertEquals(32, world.getBlock(-63, 127, 31));
		assertEquals(37, world.getBlock(-2, 17, 38));
		assertEquals(18, world.getBlock(-63, 100, -63));
		assertEquals(32, world.getBlock(60, 76, 63));
	}

	@Test
	public void testGetBrightness() {
		assertEquals(0, world.getBrightness(0, 0, 0));
		assertEquals(0, world.getBrightness(4, 5, 0));
		assertEquals(0, world.getBrightness(-8, 8, 53));
		assertEquals(0, world.getBrightness(-35, 8, 53));
	}

	@Test
	public void testSetBrightness() {
		world.setBrightness(0, 0, 0, (byte) 5);
		world.setBrightness(-32, 7, 45, (byte) 14);
		world.setBrightness(-14, 75, 29, (byte) 7);
		world.setBrightness(-64, 114, 31, (byte) 6);
		
		world.setBrightness(-63, 127, 31, (byte) 18);
		world.setBrightness(-2, 17, 38, (byte) 14);
		world.setBrightness(-63, 100, -63, (byte) 13);
		world.setBrightness(60, 76, 63, (byte) 10);
		
		assertEquals(5, world.getBrightness(0, 0, 0));
		assertEquals(14, world.getBrightness(-32, 7, 45));
		assertEquals(7, world.getBrightness(-14, 75, 29));
		assertEquals(6, world.getBrightness(-64, 114, 31));
		
		assertEquals(18, world.getBrightness(-63, 127, 31));
		assertEquals(14, world.getBrightness(-2, 17, 38));
		assertEquals(13, world.getBrightness(-63, 100, -63));
		assertEquals(10, world.getBrightness(60, 76, 63));
	}

}
