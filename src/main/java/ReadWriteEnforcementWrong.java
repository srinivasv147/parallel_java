package main.java;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ReadWriteEnforcementWrong {
	
	//the reason this is wrong is because after the reader comes in the writer can come in.
	
	private Semaphore readLock = new Semaphore(1);
	private Semaphore writeLock = new Semaphore(1);
	private volatile int commonVal = 0;
	
	public static void main(String[] args) {
		//do with read write lock and with semaphores.
		ReadWriteEnforcementWrong r = new ReadWriteEnforcementWrong();
		ArrayList<Thread> readers = new ArrayList<>();
		ArrayList<Thread> writers = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			readers.add(new Thread(r.new Reader()));
			writers.add(new Thread(r.new Writer(i)));
		}
		for(Thread thread : writers) {
			thread.start();
		}
		for(Thread thread : readers) {
			thread.start();
		}
	}
	
	private class Reader implements Runnable{

		@Override
		public void run() {
			
			try {
				readLock.acquire();
				readLock.release();//this is done so that other readers can proceed.
				System.out.println(Thread.currentThread().getName()+" is reading "+commonVal);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private class Writer implements Runnable{
		
		private int seed;
		
		public Writer(int seed) {
			this.seed = seed;
		}

		@Override
		public void run() {
			try {
				readLock.acquire();
				writeLock.acquire();
				int temp = new Random(seed).nextInt(1000);
				System.out.println(Thread.currentThread().getName()+" is writing "+temp);
				commonVal = temp;
				readLock.release();
				writeLock.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
