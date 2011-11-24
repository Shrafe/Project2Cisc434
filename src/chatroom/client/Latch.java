package chatroom.client;

public class Latch {
	private final Object latch = new Object();

	public void signal(){
		synchronized (latch) {
			latch.notify();
		}
	}

	public void await() throws InterruptedException{
		
		synchronized (latch) {
			latch.wait();
		}

	}
}
