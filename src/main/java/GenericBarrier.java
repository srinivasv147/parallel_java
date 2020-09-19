package main.java;

import java.util.concurrent.Semaphore;

public class GenericBarrier {
	
	private Semaphore barrier = new Semaphore(0);
	private Semaphore lastBarrier = new Semaphore(0);
	private int permitted;
	private volatile int count;
	
	public GenericBarrier(int n) {
		this.permitted = n;
		this.count = 0;
	}
	
	public void phaseOne() {
		synchronized(this){
			count++;
			if(count == permitted) {
				barrier.release(permitted);
			}
		}
		try {
			barrier.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void phaseTwo() {
		synchronized(this) {
			count--;
			if(count == 0) {
				lastBarrier.release(10);
			}
		}
		try {
			lastBarrier.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void barrierWait() {
		this.phaseOne();
		this.phaseTwo();
	}

}
