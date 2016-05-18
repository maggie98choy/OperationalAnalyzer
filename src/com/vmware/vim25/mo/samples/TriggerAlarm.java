package com.vmware.vim25.mo.samples;
import com.vmware.vim25.mo.samples.JavaAlarm;

public class TriggerAlarm 
{
	public static void main(String args[])
	{
		 System.out.println("Java timer is about to start");
	 	 JavaAlarm alarm = new JavaAlarm(5);
	    // boolean b_powerOff = alarm.JavaAlarm(5);
	 	 System.out.println("Remindertask is scheduled with Java timer");
	}
}
