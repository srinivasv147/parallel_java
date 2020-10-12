package main.java;

import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
	
	Semaphore[] forks;
	
	Semaphore allowPhilosophers;
	
	Thread[] philosophers;
	
	public DiningPhilosophers(int n) {
		forks = new Semaphore[n];
		for(int i = 0; i < n; i++) forks[i] = new Semaphore(1, true);
		allowPhilosophers = new Semaphore(n-1, true);
		philosophers = new Thread[n];
		for(int i = 0; i < n; i++) philosophers[i] = new Thread(new Philosopher(i, n));
	}
	
	public static void main(String[] args) {
		
		DiningPhilosophers d = new DiningPhilosophers(5);
		for(Thread thread : d.philosophers) {
			thread.start();
		}
		
	}
	
	private class Philosopher implements Runnable {
		
		int index;
		int total;
		
		public Philosopher(int i, int n) {
			index = i;
			total = n;
		}

		@Override
		public void run() {
			
			int i = 0;
			while(i < 1) {
				System.out.println("Philosopher "+index + " is thinking");
				
				try {
					allowPhilosophers.acquire();
					
					forks[index].acquire();
					forks[(index+1)%total].acquire();
					System.out.println("Philosopher "+index + " is eating");
					forks[index].release();
					forks[(index+1)%total].release();
					
					allowPhilosophers.release();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			
			
		}
		
	}

}
