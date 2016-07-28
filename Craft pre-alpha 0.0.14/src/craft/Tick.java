package craft;

public class Tick extends Thread {
	private Craft main;
	private Timer timer;
	public float a;
	
	public Tick(Craft craft, int ticksPerSecond) {
		timer = new Timer(ticksPerSecond);
		this.main = craft;
		this.setName("Tick");
	}
	
	public void run() {
		while(true) {
			timer.advanceTime();
			a = timer.a;
			for (int i = 0; i < timer.ticks; i++) {
				synchronized (this) {
					main.tick();
				}
			}
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
