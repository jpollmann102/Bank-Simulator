import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Name: Joshua Pollmann
 * Course: CNT 4714 Fall 2018
 * Assignment title: Project 2 - Synchronized, Cooperating Threads Under Locking
 * Due Date: September 23, 2018
 */


public class Main {
	
	private static class Account
	{
		private int balance = 0;
		private boolean overdraft = false;
		private Lock accessLock = new ReentrantLock();
		private Condition canWithdraw =  accessLock.newCondition();
		
		public Account()
		{
			
		}
		
		public void addFunds(String name, int amount)
		{
			accessLock.lock();
			balance += amount;
			System.out.println("Thread " + name + " deposits $" + amount + "\t\t\t\t\t\tBalance is $" + balance);
			canWithdraw.signal();
			accessLock.unlock();
		}
		
		public void withdraw(String name, int amount) throws InterruptedException
		{
			accessLock.lock();
			try
			{
				while(balance - amount < 0)
				{
					if(!overdraft) System.out.println("\t       \t\t\tThread " + name + " withdraws $" + amount + " Withdrawal - Blocked - Insufficient Funds");
					overdraft = true;
					canWithdraw.await();
				}
				
				overdraft = false;
				balance -= amount;
				System.out.println("\t       \t\t\tThread " + name + " withdraws $" + amount + "\t\tBalance is $" + balance);
				
			}finally
			{
				accessLock.unlock();
			}
		}
		
		public int getBalance()
		{
			return this.balance;
		}
	}

	private static class depositThread extends Thread
	{
		private static Account acc;
		private String name;
		private Random r;
		
		public depositThread(Account acc, String name)
		{
			this.acc = acc;
			this.name = name;
			r = new Random();
		}
		
		public void run()
		{
			while(true)
			{
				int thisDeposit = r.nextInt(199) + 1;
				
				acc.addFunds(name, thisDeposit);
				
				try {
					sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static class withdrawThread extends Thread
	{
		private static Account acc;
		private String name;
		private Random r;
		
		public withdrawThread(Account acc, String name)
		{
			this.acc = acc;
			this.name = name;
			r = new Random();
		}
		
		public void run()
		{
			while(true)
			{
				int thisWithdraw = r.nextInt(49) + 1;

				try {
					acc.withdraw(name, thisWithdraw);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		Account mainAccount = new Account();
		System.out.println("Deposit Threads\t\t\tWithdrawal Threads\t\t\tBalance");
		System.out.println("---------------\t\t\t---------------\t\t\t---------------");
		
		for(int i = 1; i < 5; i++)
		{
			Thread t = new depositThread(mainAccount, "D" + i);
			t.start();
		}
		
		for(int j = 1; j < 7; j++)
		{
			Thread u = new withdrawThread(mainAccount, "W" + j);
			u.start();
		}
	}
}
