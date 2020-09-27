package main.java;

import java.util.ArrayList;

public class DancerQueue {
	
	public static void main(String[] args) {
		DancerQueue d = new DancerQueue();
		QueueOfThreads leaderQ = new QueueOfThreads();
		QueueOfThreads followerQ = new QueueOfThreads();
		ArrayList<Thread> leaders = new ArrayList<>();
		ArrayList<Thread> followers = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			leaders.add(new Thread(d.new Dancer(leaderQ, followerQ), "leaderQ "+i));
			followers.add(new Thread(d.new Dancer(followerQ, leaderQ),"followerQ "+i));
		}
		for(Thread thread : leaders) {
			thread.start();
		}
		for(Thread thread : followers) {
			thread.start();
		}
	}
	
	class Dancer implements Runnable{
		
		private QueueOfThreads ourQ;
		private QueueOfThreads partnerQ;
		
		public Dancer(QueueOfThreads queue1, QueueOfThreads queue2) {
			this.ourQ = queue1;
			this.partnerQ = queue2;
		}

		@Override
		public void run() {
			if(!partnerQ.release()) {
				try {
					ourQ.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println(Thread.currentThread().getName()+" released without waiting");
			}
		}
		
	}
	
}
