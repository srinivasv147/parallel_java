package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class NonStarveMutex {
	
	// here we are trying to prevent threads from looping around
	// in such a way that one thread can starve on a mutex.
	
	Semaphore t1 = new Semaphore(1);
	Semaphore t2 = new Semaphore(0);
	volatile int room1 = 0;
	volatile int room2 = 0;
	Semaphore mutex = new Semaphore(1);
	
	public static void main(String[] args) {
		
		NonStarveMutex n = new NonStarveMutex();
		ArrayList<Thread> threads = new ArrayList<>();
		for(int i = 0; i < 10; i++) threads.add(new Thread(n.new SimpleNoStarve()));
		for(Thread thread : threads) thread.start();
		
	}
	
	class SimpleNoStarve implements Runnable{

		@Override
		public void run() {
			int i = 2;
			while(i > 0) {
				try {
					
					mutex.acquire();
						room1++;
					mutex.release();
					t1.acquire();
					mutex.acquire();
						room2++;
						room1--;
					mutex.release();
					if(room1 == 0) t2.release();
					else t1.release();
					t2.acquire();
					
					//critical section
					mutex.acquire();
						System.out.println(Thread.currentThread().getName()
								+" in the critical section");
					mutex.release();
					
					mutex.acquire();
						room2--;
					mutex.release();
					
					if(room2 == 0) t1.release();
					else t2.release();
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i--;
			}
			
		}
		
	}

}
