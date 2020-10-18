package main.java;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class CigarSmokersProblem {
	
	private static Semaphore tobaco = new Semaphore(0);
	private static Semaphore match = new Semaphore(0);
	private static Semaphore paper = new Semaphore(0);
	private static Semaphore agent = new Semaphore(1);
	
	public static void main(String[] args) {
		
		CigarSmokersProblem o = new CigarSmokersProblem();
		
		ArrayList<Thread> agents = new ArrayList<>();
		ArrayList<Thread> smokers = new ArrayList<>();
		
		agents.add(new Thread(o.new Agent(tobaco, match, "tobaco", "match")));
		agents.add(new Thread(o.new Agent(tobaco, paper, "tobaco", "paper")));
		agents.add(new Thread(o.new Agent(paper, match, "paper", "match")));
		
		smokers.add(new Thread(o.new Smoker(tobaco, match, "tobaco", "match")));
		smokers.add(new Thread(o.new Smoker(match, paper, "match", "paper")));
		smokers.add(new Thread(o.new Smoker(paper, tobaco, "paper", "tobaco")));
		
		for(Thread agent : agents) agent.start();
		for(Thread smoker : smokers) smoker.start();
		
		
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
					System.out.println("agent releasing "+one+" "+two);
					agent.acquire();
					ing1.release();
					ing2.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			
		}
		
	}
	
	private class Smoker implements Runnable {
		
		private Semaphore ing1;
		private Semaphore ing2;
		private String one;
		private String two;
		
		public Smoker(Semaphore ing1, Semaphore ing2, String one, String two) {
			this.ing1 = ing1;
			this.ing2 = ing2;
			this.one = one;
			this.two = two;
		}

		@Override
		public void run() {
			
			int  i = 0;
			while(i < 5) {
				
				try {
					System.out.println("acquiring "+one+" "+two);
					ing1.acquire();
					ing2.acquire();// can use tryacquire to release 
					// the first if second is not available
					agent.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			
		}
		
	}

}
