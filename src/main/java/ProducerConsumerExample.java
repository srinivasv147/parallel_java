package main.java;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ProducerConsumerExample {
	
	//java stack is thread safe as it is extending vector.
	//java queue is an interface and the thread safety is not there if we use linked list.
	Queue<String> q = new LinkedList<>();// this is not thread safe.
	Semaphore qLock = new Semaphore(1);
	Semaphore items = new Semaphore(0);//this is there to block consumers to not consume
	// cpu if there are no items, instead of executing the while loop.
	int maxsize = 5;
	Semaphore maxItems = new Semaphore(maxsize);

	public static void main(String[] args) throws InterruptedException {
		
		ProducerConsumerExample e = new ProducerConsumerExample();
		ArrayList<Thread> producers = new ArrayList<>();
		ArrayList<Thread> consumers = new ArrayList<>();
		for(int i = 0; i<10;i++) {
			producers.add(new Thread(e.new Producer(i)));
			consumers.add(new Thread(e.new Consumer(i)));
		}
		for(Thread thread : producers) {
			thread.start();
		}
		Thread.sleep(10000);
		for(Thread thread : consumers) {
			thread.start();
		}
		
	}
	
	public class Producer implements Runnable{
		
		private int randomWaitSeed;
		
		public Producer(int seed) {
			this.randomWaitSeed = seed;
		}
		
		@Override
		public void run() {
			int i = 0;
			try {
				while(i < 1) {
					Thread.sleep(new Random(randomWaitSeed).nextInt(2000));
					String message = Thread.currentThread().getName()+" adding message";
					maxItems.acquire(); 
					qLock.acquire();
						q.add(message);
						System.out.println("producer "+Thread.currentThread().getName()+" has added and the queue size is "+q.size());
					qLock.release();
					items.release();
					i++;
				}
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				qLock.release();
			}
		}
		
	}
	
	public class Consumer implements Runnable{
		
		private int randomWaitSeed;
		
		public Consumer(int seed) {
			this.randomWaitSeed = seed;
		}

		@Override
		public void run() {
			
			int i = 0;
			try {
				while(i < 1) {
					Thread.sleep(new Random(randomWaitSeed).nextInt(2000));
					items.acquire(); 
					qLock.acquire();
						System.out.println("consumer is consuming "+q.poll());
					qLock.release();
					maxItems.release();//releasing after getting out of queue lock
					// will get more performence as another thread will not have to
					//wait again.
					i++;
				}
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				qLock.release();
			}
			
		}
		
	}
	
}
