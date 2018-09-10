import java.util.Random;

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
		
		public Account()
		{
			
		}
		
		public void addFunds(int amount)
		{
			balance += amount;
		}
		
		public boolean withdraw(int amount)
		{
			if(balance - amount < 0) return false;
			balance -= amount;
			return true;
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
				System.out.println("Thread " + this.name + " deposits $" + thisDeposit + "\t\t\tBalance is $" + acc.getBalance());
				acc.addFunds(thisDeposit);
				
				try {
					sleep(3);
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
				boolean hold = false;
				while(!acc.withdraw(thisWithdraw))
				{
					if(!hold) System.out.println("Thread " + this.name + " withdraws $" + thisWithdraw + "Withdrawal - Blocked - Insufficient Funds");
					hold = true;
				}
				System.out.println("\tThread " + this.name + " withdraws $" + thisWithdraw + "\tBalance is $" + acc.getBalance());
			}
		}
	}
	
	public static void main(String[] args)
	{
		Account mainAccount = new Account();
		System.out.println("Deposit Threads\tWithdrawal Threads\t Balance");
		System.out.println("----------\t----------\t----------");
		
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
