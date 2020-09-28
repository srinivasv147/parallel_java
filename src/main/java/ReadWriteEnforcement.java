package main.java;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class ReadWriteEnforcement {
	
	private Semaphore mutex = new Semaphore(1);
	private Semaphore zeroReaders = new Semaphore(1);
	private volatile int numReaders = 0;
	private volatile int commonVal = 0;
	
	public static void main(String[] args) {
		//do with read write lock and with semaphores.
		ReadWriteEnforcement r = new ReadWriteEnforcement();
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
				mutex.acquire();
					numReaders++;
					if(numReaders == 1)//first reader
						zeroReaders.acquire();
				mutex.release();
				System.out.println(Thread.currentThread().getName()+" is reading "+commonVal);
				mutex.acquire();
					numReaders--;
					if(numReaders == 0)//last reader
						zeroReaders.release();
				mutex.release();
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
				zeroReaders.acquire();
					int temp = new Random(seed).nextInt(1000);
					System.out.println(Thread.currentThread().getName()+" is writing "+temp);
					commonVal = temp;
				zeroReaders.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
