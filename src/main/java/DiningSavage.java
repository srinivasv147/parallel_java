package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DiningSavage {
	
	private static final int MAX_SERVINGS = 10;//max servings in pot.
	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore wakeCook = new Semaphore(0);
	private static Semaphore cookDone = new Semaphore(0);
	private volatile int servingsLeft = MAX_SERVINGS;
	
	public static void main(String[] args) {
		
		DiningSavage d = new DiningSavage();
		
		ArrayList<Thread> savages = new ArrayList<>();
		for(int i = 0; i <100; i++) {
			savages.add(new Thread(d.new Savage()));
		}
		Thread cook = new Thread(d.new Cook());
		cook.setDaemon(true);
		cook.start();
		for(Thread thread : savages) thread.start();
	}
	
	private class Savage implements Runnable {

		@Override
		public void run() {
			int i = 0;
			while(i < 5) {
				try {
					mutex.acquire();
						if(servingsLeft == 0) {
							System.out.println("calling cook");
							wakeCook.release();
							cookDone.acquire();
						}
						servingsLeft--;
					mutex.release();
					System.out.println(Thread.currentThread().getName()+" is eating");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			
			
		}
		
	}
	
	private class Cook implements Runnable {

		@Override
		public void run() {
			
			while(true) {
				try {
					wakeCook.acquire();
					servingsLeft = MAX_SERVINGS;
					cookDone.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
