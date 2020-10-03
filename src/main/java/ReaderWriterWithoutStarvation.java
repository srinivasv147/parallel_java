package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ReaderWriterWithoutStarvation {
	
	Semaphore mutex = new Semaphore(1);
	Semaphore lockRoom = new Semaphore(1);
	Semaphore writerPref = new Semaphore(1);
	private volatile int count = 0;
	private volatile int writerCount = 0;
	private volatile int value = 0;
	
	public static void main(String [] args) {
		ReaderWriterWithoutStarvation r = new ReaderWriterWithoutStarvation();
		ArrayList<Thread> readers = new ArrayList<>();
		ArrayList<Thread> writers = new ArrayList<>();
		for(int i = 0; i< 10; i++) {
			readers.add(new Thread(r.new Reader()));
			writers.add(new Thread(r.new Writer(i)));
		}
		for(int i = 0; i < 5; i ++) {
			readers.get(i).start();
			writers.get(i).start();
		}
		for(int i = 5; i < 10; i++) {
			writers.get(i).start();
			readers.get(i).start();
		}
	}
	
	class Reader implements Runnable {

		@Override
		public void run() {
			
			try {
				writerPref.acquire();
				writerPref.release();
				mutex.acquire();
					count++;
					if(count == 1) lockRoom.acquire();
				mutex.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(value + " is the value read");
			
			try {
				mutex.acquire();
					count--;
					if(count == 0) lockRoom.release();
				mutex.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class Writer implements Runnable {
		
		int writeValue;
		
		public Writer(int val) {
			this.writeValue = val;
		}

		@Override
		public void run() {
			
			try {
				mutex.acquire();
					writerCount++;
					if(writerCount == 1) writerPref.acquire();
				mutex.release();
				lockRoom.acquire();
					value = this.writeValue;
					System.out.println("new value written is "+value);
				lockRoom.release();
				mutex.acquire();
					writerCount--;
					if(writerCount == 0) writerPref.release();
				mutex.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
