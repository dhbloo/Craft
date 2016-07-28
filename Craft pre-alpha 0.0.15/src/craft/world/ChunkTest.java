package craft.world;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ChunkTest {
	
	private static World world = new World();
	private static Chunk chunk;

	@Before
	public void setUp() throws Exception {
		world.length = 128;
		world.width = 128;
		world.height = 128;
		chunk = new EmptyChunk(world, 0, 0, 0);
	}

	@Test
	public void testChunk() {
		chunk = new EmptyChunk(world, 0, 0, 0);
	}

	@Test
	public void testSetDate() {
		BlockStorage[] blockStorages = new BlockStorage[4];
		for (int i = 0; i < 4; i++) {
			blockStorages[i] = new EmptyBlockStorage();
			blockStorages[i].setBlockID(5, 5, 5, (byte) 4);
			blockStorages[i].setBlockID(5, 5, 0, (byte) 7);
			blockStorages[i].setBlockID(0, 0, 0, (byte) 14);
		}
		chunk.setDate(blockStorages);
		assertEquals(14, chunk.getBlock(0, 0, 0));
		assertEquals(4, chunk.getBlock(5, 5, 5));
		assertEquals(7, chunk.getBlock(5, 5, 0));
		assertEquals(4, chunk.getBlock(5, 21, 5));
		assertEquals(7, chunk.getBlock(5, 21, 0));
		assertEquals(14, chunk.getBlock(0, 16, 0));
	}

	@Test
	public void testGetBlock() {
		assertEquals(0, chunk.getBlock(0, 0, 0));
		assertEquals(0, chunk.getBlock(4, 10, 2));
	}

	@Test
	public void testSetBlock() {
		chunk.setBlock(0, 0, 0, (byte) 2);
		chunk.setBlock(3, 14, 7, (byte) 5);
		chunk.setBlock(8, 4, 13, (byte) 8);
		assertEquals(2, chunk.getBlock(0, 0, 0));
		assertEquals(5, chunk.getBlock(3, 14, 7));
		assertEquals(8, chunk.getBlock(8, 4, 13));
	}

	@Test
	public void testGetBlockBrightness() {
		assertEquals(0, chunk.getBlockBrightness(0, 0, 0));
		assertEquals(0, chunk.getBlockBrightness(2, 14, 15));
		assertEquals(0, chunk.getBlockBrightness(4, 2, 8));
	}

	@Test
	public void testSetBlockBrightness() {
		chunk.setBlockBrightness(0, 0, 0, (byte) 7);
		chunk.setBlockBrightness(3, 15, 7, (byte) 5);
		chunk.setBlockBrightness(10, 8, 14, (byte) 15);
		assertEquals(7, chunk.getBlockBrightness(0, 0, 0));
		assertEquals(5, chunk.getBlockBrightness(3, 15, 7));
		assertEquals(15, chunk.getBlockBrightness(10, 8, 14));
	}

	@Test
	public void testIsLightBlock() {
		chunk.setBlock(0, 1, 0, (byte) 2);
		chunk.setBlock(15, 3, 1, (byte) 0);
		chunk.setBlock(5, 1, 1, (byte) 1);
		assertTrue(!chunk.isLightBlock(0, 1, 0));
		assertTrue(chunk.isLightBlock(15, 3, 1));
		assertTrue(!chunk.isLightBlock(5, 1, 1));
		
	}

}
