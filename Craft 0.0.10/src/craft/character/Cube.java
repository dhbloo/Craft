package craft.character;

import org.lwjgl.opengl.GL11;

public class Cube {
	private Vertex[] vertices;
	private Polygon[] polygons;
	private int xTexOffs;
	private int yTexOffs;
	public float x;
	public float y;
	public float z;
	public float xRot;
	public float yRot;
	public float zRot;
	private boolean compiled = false;
	private int list = 0;

	public Cube(int xTexOffs, int yTexOffs) {
		this.xTexOffs = xTexOffs;
		this.yTexOffs = yTexOffs;
	}

	public void setTexOffs(int xTexOffs, int yTexOffs) {
		this.xTexOffs = xTexOffs;
		this.yTexOffs = yTexOffs;
	}

	public void addBox(float x0, float y0, float z0, int w, int h, int d) {
		vertices = new Vertex[8];
		polygons = new Polygon[6];

		float x1 = x0 + w;
		float y1 = y0 + h;
		float z1 = z0 + d;

		Vertex u0 = new Vertex(x0, y0, z0, 0.0F, 0.0F);
		Vertex u1 = new Vertex(x1, y0, z0, 0.0F, 8.0F);
		Vertex u2 = new Vertex(x1, y1, z0, 8.0F, 8.0F);
		Vertex u3 = new Vertex(x0, y1, z0, 8.0F, 0.0F);

		Vertex l0 = new Vertex(x0, y0, z1, 0.0F, 0.0F);
		Vertex l1 = new Vertex(x1, y0, z1, 0.0F, 8.0F);
		Vertex l2 = new Vertex(x1, y1, z1, 8.0F, 8.0F);
		Vertex l3 = new Vertex(x0, y1, z1, 8.0F, 0.0F);

		vertices[0] = u0;
		vertices[1] = u1;
		vertices[2] = u2;
		vertices[3] = u3;
		vertices[4] = l0;
		vertices[5] = l1;
		vertices[6] = l2;
		vertices[7] = l3;

		polygons[0] = new Polygon(new Vertex[] { l1, u1, u2, l2 },
				xTexOffs + d + w, yTexOffs + d, xTexOffs + d + w
						+ d, yTexOffs + d + h);
		polygons[1] = new Polygon(new Vertex[] { u0, l0, l3, u3 },
				xTexOffs + 0, yTexOffs + d, xTexOffs + d,
				yTexOffs + d + h);

		polygons[2] = new Polygon(new Vertex[] { l1, l0, u0, u1 },
				xTexOffs + d, yTexOffs + 0, xTexOffs + d + w,
				yTexOffs + d);
		polygons[3] = new Polygon(new Vertex[] { u2, u3, l3, l2 },
				xTexOffs + d + w, yTexOffs + 0, xTexOffs + d + w
						+ w, yTexOffs + d);

		polygons[4] = new Polygon(new Vertex[] { u1, u0, u3, u2 },
				xTexOffs + d, yTexOffs + d, xTexOffs + d + w,
				yTexOffs + d + h);
		polygons[5] = new Polygon(new Vertex[] { l0, l1, l2, l3 },
				xTexOffs + d + w + d, yTexOffs + d, xTexOffs + d
						+ w + d + w, yTexOffs + d + h);
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void render() {
		if (!compiled)
			compile();

		float c = 57.29578F;
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(zRot * c, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(yRot * c, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(xRot * c, 1.0F, 0.0F, 0.0F);

		GL11.glCallList(list);
		GL11.glPopMatrix();
	}

	private void compile() {
		list = GL11.glGenLists(1);
		GL11.glNewList(list, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < polygons.length; i++) {
			polygons[i].render();
		}
		GL11.glEnd();
		GL11.glEndList();
		compiled = true;
	}
}
