package craft.environment;

import org.lwjgl.opengl.GL11;

public class Sky {
	public static final int M = 30, N = 30;
	public int RADIUS;
	private int skyList;

	public Sky(int radius) {
		skyList = GL11.glGenLists(1);
		RADIUS = radius;
		compileSkyList();
	}
	
	private void compileSkyList() {
		GL11.glNewList(skyList, GL11.GL_COMPILE);
		GL11.glPushMatrix();
		GL11.glRotatef(-90, 1.0F, 0.0F, 0.0F);
		float step_z = (float) (Math.PI / M);
		float step_xy = (float) (2 * Math.PI / N);
		float x[] = new float[4], y[] = new float[4], z[] = new float[4];

		float angle_z = 0.0F;
		float angle_xy = 0.0F;
		int i = 0, j = 0;
		GL11.glBegin(GL11.GL_QUADS);
		for (i = 0; i < M; i++) {
			angle_z = i * step_z;

			for (j = 0; j < N; j++) {
				angle_xy = j * step_xy;

				x[0] = (float) (RADIUS * Math.sin(angle_z) * Math.cos(angle_xy));
				y[0] = (float) (RADIUS * Math.sin(angle_z) * Math.sin(angle_xy));
				z[0] = (float) (RADIUS * Math.cos(angle_z));

				x[1] = (float) (RADIUS * Math.sin(angle_z + step_z) * Math.cos(angle_xy));
				y[1] = (float) (RADIUS * Math.sin(angle_z + step_z) * Math.sin(angle_xy));
				z[1] = (float) (RADIUS * Math.cos(angle_z + step_z));

				x[2] = (float) (RADIUS * Math.sin(angle_z + step_z) * Math.cos(angle_xy + step_xy));
				y[2] = (float) (RADIUS * Math.sin(angle_z + step_z) * Math.sin(angle_xy + step_xy));
				z[2] = (float) (RADIUS * Math.cos(angle_z + step_z));

				x[3] = (float) (RADIUS * Math.sin(angle_z) * Math.cos(angle_xy + step_xy));
				y[3] = (float) (RADIUS * Math.sin(angle_z) * Math.sin(angle_xy + step_xy));
				z[3] = (float) (RADIUS * Math.cos(angle_z));

				for (int k = 3; k >= 0; k--) {
					if (z[k] < -110.0F)
						GL11.glColor3ub((byte) 0, (byte) 114, (byte) 255);
					else if (z[k] < -60.0F)
						GL11.glColor3ub((byte) 156, (byte) 196, (byte) 223);
					else if (z[k] < 116.0F)
						GL11.glColor3ub((byte) 235, (byte) 235, (byte) 235);
					else
						GL11.glColor3ub((byte) 150, (byte) 195, (byte) 224);
					GL11.glVertex3f(x[k], y[k], z[k]);
				}
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEndList();
	}
	
	public void render() {
		GL11.glCallList(skyList);
	}

}
