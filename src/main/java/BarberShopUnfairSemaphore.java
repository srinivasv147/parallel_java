package main.java;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;



public class BarberShopUnfairSemaphore {
	
	private static volatile int waiting = 0;
	private static final int space = 10;
	private static Queue<Semaphore> waitList = new LinkedList<>();
	private static Semaphore barberSleep = new Semaphore(0);
	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore customerDone = new Semaphore(0);
	
	
	public static void main (String[] args) {
		
		BarberShopUnfairSemaphore b = new BarberShopUnfairSemaphore();
		
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
					System.out.println("barber is waiting");
					barberSleep.acquire();
					System.out.println("cutting hair");
					customerDone.acquire();
					System.out.println("barber released by customer");
					mutex.acquire();
					Semaphore nextCust = waitList.poll();
					System.out.println("semaphore is "+nextCust);
					if(nextCust != null) nextCust.release();
					mutex.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class Customer implements Runnable {

		@Override
		public void run() {
			
			try {
				int i = 0;
				while(i < 1) {
					mutex.acquire();
					if(waiting == space) {
						System.out.println(Thread.currentThread().getName()+" is leaving");
						mutex.release();
						break;
					}
					waiting++;
					Semaphore curCust;
					if(waiting == 1) {
						curCust = new Semaphore(1);
						System.out.println(Thread.currentThread().getName()+" is first in the queue");
					}
					else {
						curCust = new Semaphore(0);
						waitList.add(curCust);
						System.out.println(Thread.currentThread().getName()+" is "+waitList.size()+" in the queue");
					}
					mutex.release();
					curCust.acquire();
					barberSleep.release();
					System.out.println(Thread.currentThread().getName()+" is getting hair cut");
					mutex.acquire();
					waiting--;
					mutex.release();
					customerDone.release();
					i++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
