package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
	
	volatile int count = 0;
	ReentrantLock lock = new ReentrantLock();
	Semaphore barrier = new Semaphore(0);
	Semaphore nextBarrier = new Semaphore(1);
	
	public static void main(String[] args) throws InterruptedException {
		
		Barrier b = new Barrier();
		ArrayList<Thread> threads = new ArrayList<>();
		ArrayList<Thread> threads2 = new ArrayList<>();
		
		for(int i = 0; i < 10; i++) {
			threads.add(new Thread(b.new Runner()));
			threads2.add(new Thread(b.new Runner()));
		}
		
		for(Thread thread : threads) thread.start();
		//for(Thread thread : threads) thread.join();
		//for(Thread thread : threads2) thread.start();
		//dfor(Thread thread : threads2) thread.join();
	}
	
	private class Runner implements Runnable{

		@Override
		public void run() {
			int i = 0;
			while(i < 2) {
				System.out.println("this is printed first "+count);
				lock.lock();
				count++;
				if(count == 10) {
					//System.out.println(Thread.currentThread().getName()+" releasing lock ");
					barrier.release();
					try {
						nextBarrier.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lock.unlock();
				try {
					//System.out.println(Thread.currentThread().getName()+" acquiring lock ");
					barrier.acquire();
					//System.out.println(Thread.currentThread().getName()+" releasing lock ");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				barrier.release();
				System.out.println("this is printed next");
				lock.lock();
				count--;
				if(count == 0) {
					try {
						barrier.acquire();
						nextBarrier.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lock.unlock();
				
				try {
					nextBarrier.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nextBarrier.release();
				i++;
			}
			
		}
		
	}

}
