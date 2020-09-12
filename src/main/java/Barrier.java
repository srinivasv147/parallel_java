package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
	
	int count = 0;
	
	public static void main(String[] args) throws InterruptedException {
		
		Barrier b = new Barrier();
		ArrayList<Thread> threads = new ArrayList<>();
		for(int i = 0; i < 10; i++) threads.add(new Thread(b.new Runner()));
		for(Thread thread : threads) thread.start();
		for(Thread thread : threads) thread.join();
	}
	
	private class Runner implements Runnable{

		@Override
		public void run() {
			
			ReentrantLock lock = new ReentrantLock();
			Semaphore barrier = new Semaphore(0);
			lock.lock();
			count++;
			lock.unlock();
			if(count == 10) barrier.release();
			System.out.println("this is printed first");
			try {
				barrier.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			barrier.release();
			System.out.println("this is printed next");
			
			
		}
		
	}

}
