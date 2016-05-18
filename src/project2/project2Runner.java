package project2;

import java.net.UnknownHostException;
import java.sql.SQLException;

import project2.LogCollection;
import project2.AnalyzerEngine;


public class project2Runner 
{
	
	
	public static void main(String[]args) throws Exception
	{
		new Thread()
		{
			public void run()
			{
					LogCollection lc = null;
					try 
					{
						System.out.println("*** Collecting VM stats ***");
						lc = new LogCollection();
					} catch (UnknownHostException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						lc.getSystemData();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}				
					
			};
		}.start();
		
		new Thread()
		{
			public void run()
			{
				
				AnalyzerEngine ae = null;
				try 
				{
					System.out.println("*** High-level data processing ***");
					ae = new AnalyzerEngine();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ae.RunAnalyzer();					
			};
		}.start();
		

	}
}

