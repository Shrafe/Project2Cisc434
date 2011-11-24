package chatroom.client;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

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
