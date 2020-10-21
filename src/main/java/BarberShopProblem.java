package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class BarberShopProblem {
	
	private static Semaphore barberSleep = new Semaphore(0);
	private static Semaphore waitingForBarber = new Semaphore(1, true);
	private static Semaphore mutex = new Semaphore(1);
	private static final int MAX_WAIT = 10;
	private volatile static int curWait = 0;
	
	public static void main(String[] args) {
		
		BarberShopProblem b = new BarberShopProblem();
		ArrayList<Thread> customers = new ArrayList<>();
		for(int i = 0; i < 100; i++) customers.add(new Thread(b.new Customer()));
		
		Thread barber = new Thread(b.new Barber());
		barber.setDaemon(true);
		barber.start();
		for(Thread thread : customers) thread.start();
		
	}
	
	private class Barber implements Runnable {

		@Override
		public void run() {
			
			while(true) {
				try {
					barberSleep.acquire();
					System.out.println("barber is cutting hair");
					waitingForBarber.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class Customer implements Runnable {

		@Override
		public void run() {
			int i = 0;
			while(i < 1) {
				try {
					mutex.acquire();
					if(curWait == MAX_WAIT) {
						System.out.println("could not find waiting space");
						//mutex.release();
						break;
					}
					curWait++;
					mutex.release();
					waitingForBarber.acquire();
					barberSleep.release();
					mutex.acquire();
					curWait--;
					mutex.release();
					System.out.println("getting hair cut");
					//i++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
