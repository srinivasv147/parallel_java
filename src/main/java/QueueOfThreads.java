package main.java;


public class QueueOfThreads {
	
	private volatile int count = 0;
	
	public synchronized void acquire() throws InterruptedException {
		count++;
		System.out.println(Thread.currentThread().getName()+" waiting");
		wait();
		System.out.println(Thread.currentThread().getName()+" released");
	}
	
	public synchronized boolean release() {
		if(count <= 0) return false;
		else {
			notify();
			count--;
			return true;
		}
	}
}
