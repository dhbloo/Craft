package craft.character;

public class CharModel {
	public Cube head;
	public Cube body;
	public Cube arm0;
	public Cube arm1;
	public Cube leg0;
	public Cube leg1;

	public CharModel() {
		head = new Cube(0, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);

		body = new Cube(16, 16);
		body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);

		arm0 = new Cube(40, 16);
		arm0.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);
		arm0.setPos(-5.0F, 2.0F, 0.0F);

		arm1 = new Cube(40, 16);
		arm1.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);
		arm1.setPos(5.0F, 2.0F, 0.0F);

		leg0 = new Cube(0, 16);
		leg0.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
		leg0.setPos(-2.0F, 12.0F, 0.0F);

		leg1 = new Cube(0, 16);
		leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
		leg1.setPos(2.0F, 12.0F, 0.0F);
	}

	public void render(float time) {
		head.yRot = ((float) Math.sin(time * 0.83D) * 1.0F);
		head.xRot = ((float) Math.sin(time) * 0.8F);

		arm0.xRot = ((float) Math.sin(time * 0.6662D + Math.PI) * 2.0F);
		arm0.zRot = ((float) (Math.sin(time * 0.2312D) + 1.0D) * 1.0F);

		arm1.xRot = ((float) Math.sin(time * 0.6662D) * 2.0F);
		arm1.zRot = ((float) (Math.sin(time * 0.2812D) - 1.0D) * 1.0F);

		leg0.xRot = ((float) Math.sin(time * 0.6662D) * 1.4F);
		leg1.xRot = ((float) Math.sin(time * 0.6662D + Math.PI) * 1.4F);

		head.render();
		body.render();
		arm0.render();
		arm1.render();
		leg0.render();
		leg1.render();
	}
}
