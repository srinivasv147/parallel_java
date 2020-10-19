package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class CorrectCigarSmoker {
	
	private static Semaphore tobaco = new Semaphore(0, true);
	private static Semaphore match = new Semaphore(0, true);
	private static Semaphore paper = new Semaphore(0, true);
	private static Semaphore agent = new Semaphore(1);
	private static Semaphore smoker1 = new Semaphore(0);
	private static Semaphore smoker2 = new Semaphore(0);
	private static Semaphore smoker3 = new Semaphore(0);
	private static Boolean isTobaco = false, isPaper = false, isMatch = false;
	private static Semaphore mutex = new Semaphore(1);
	
	public static void main(String[] args) {
		
		CorrectCigarSmoker o = new CorrectCigarSmoker();
		
		ArrayList<Thread> agents = new ArrayList<>();
		ArrayList<Thread> smokers = new ArrayList<>();
		ArrayList<Thread> pushers = new ArrayList<>();
		
		agents.add(new Thread(o.new Agent(tobaco, match, "tobaco", "match")));
		agents.add(new Thread(o.new Agent(tobaco, paper, "tobaco", "paper")));
		agents.add(new Thread(o.new Agent(paper, match, "paper", "match")));
		
		smokers.add(new Thread(o.new Smoker(smoker1, "one")));
		smokers.add(new Thread(o.new Smoker(smoker2, "two")));
		smokers.add(new Thread(o.new Smoker(smoker3, "three")));
		
		pushers.add(new Thread(o.new Pusher(tobaco,  "tobaco")));
		pushers.add(new Thread(o.new Pusher(match,  "match")));
		pushers.add(new Thread(o.new Pusher(paper,  "paper")));
		
		for(Thread agent : agents) agent.start();
		for(Thread smoker : smokers) smoker.start();
		for(Thread pusher : pushers) pusher.start();
		
	}
	
	private class Agent implements Runnable {
		
		private Semaphore ing1;
		private Semaphore ing2;
		private String one;
		private String two;
		
		public Agent(Semaphore ing1, Semaphore ing2, String one, String two) {
			this.ing1 = ing1;
			this.ing2 = ing2;
			this.one = one;
			this.two = two;
		}

		@Override
		public void run() {
			int i = 0;
			while(i < 5) {
				try {
					agent.acquire();
					ing1.release();
					ing2.release();
					System.out.println("agent released "+one+" "+two+" "+ing1.availablePermits()+" "+ing2.availablePermits());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			
		}
		
	}
	
	private class Smoker implements Runnable {
		
		private Semaphore smokerSem;
		private String self;
		
		public Smoker(Semaphore sem, String self) {
			this.smokerSem = sem;
			this.self = self;
		}

		@Override
		public void run() {
			
			int  i = 0;
			while(i < 5) {
				
				try {
					smokerSem.acquire();
					agent.release();
					System.out.println("smoker "+self+" is smoking");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			
		}
		
	}
	
	private class Pusher implements Runnable {
		
		private Semaphore ing1;
		private String ing;
		
		public Pusher(Semaphore ing1, String ing) {
			this.ing1 = ing1;
			this.ing = ing;
		}

		@Override
		public void run() {
			
			try {
				while(true) {
					ing1.acquire();
					System.out.println(ing+" acquired");
					mutex.acquire();
						if(ing.equals("tobaco")) isTobaco = true;
						if(ing.equals("match")) isMatch = true;
						if(ing.equals("paper")) isPaper = true;
						System.out.println(isTobaco+" "+isMatch+" "+isPaper);
						if(isMatch && isPaper) {
							isMatch = false; isPaper = false;
							System.out.println("releasing third smoker");
							smoker3.release();
						}
						if(isTobaco && isMatch) {
							isTobaco = false;isMatch  = false;
							System.out.println("releasing first smoker");
							smoker1.release();
						}
						if(isTobaco && isPaper) {
							isTobaco = false;isPaper  = false;
							System.out.println("releasing second smoker");
							smoker2.release();
						}
					mutex.release();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
	}

}
