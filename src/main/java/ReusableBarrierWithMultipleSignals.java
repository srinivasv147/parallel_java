package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class ReusableBarrierWithMultipleSignals {
	
	volatile int count = 0;
	ReentrantLock lock = new ReentrantLock();
	Semaphore barrier = new Semaphore(0);
	Semaphore lastBarrier = new Semaphore(0);
	GenericBarrier genericBarrier = new GenericBarrier(10);
	
	public static void main(String[] args) {
		ReusableBarrierWithMultipleSignals rb = new ReusableBarrierWithMultipleSignals();
		ArrayList<Thread> threads1 = new ArrayList<>();
		ArrayList<Thread> threads2 = new ArrayList<>();
		for(int i =0; i< 10; i++) {
			threads1.add(new Thread(rb.new MultBarrier()));
		}
		//for(Thread thread : threads1) thread.start();
		for(int i = 0; i < 10; i++) {
			threads2.add(new Thread(rb.new GenBarrierDemo()));
		}
		for(Thread thread : threads2) thread.start();
	}
	
	public class GenBarrierDemo implements Runnable{

		@Override
		public void run() {
			
			System.out.println("print this first");
			genericBarrier.barrierWait();
			System.out.println("print this last");
			
		}
		
	}
	
	public class MultBarrier implements Runnable{

		@Override
		public void run() {
			int itr = 2;
			while(itr > 0) {
				
				System.out.println("this must be printed first");
				// all code that needs to be executed before the barrier must be ended here
				lock.lock();
					count++;
					if(count == 10) {
						barrier.release(10);
					}
				lock.unlock();
				try {
					//semaphore wait is like any other object wait
					// it can be called only in synchronous block
					// otherwise wait doesn't make sense and java doesn't allow it.
					barrier.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.lock();
					count--;
					if(count == 0) {
						lastBarrier.release(10);
					}
				lock.unlock();
				try {
					lastBarrier.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// remember that the barrier open and close can be placed together
				// this will make the code wait after the first part
				// but not after the second part.
				System.out.println("this must be printed after the barrier");
				//we can also make the barrier into a class and use it.
				itr--;
			}
			
		}
		
	}
}
