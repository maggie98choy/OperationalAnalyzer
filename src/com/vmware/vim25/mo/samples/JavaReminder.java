package com.vmware.vim25.mo.samples;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


 public class JavaReminder {
    Timer timer;
    VirtualMachine vm;

    public JavaReminder(int seconds, VirtualMachine vMachine) {
    	vm = vMachine;
    	timer = new Timer();  //At this line a new Thread will be created
        timer.schedule(new RemindTask(), seconds*1000); //delay in milliseconds
        System.out.println("Timer to turn off VM is scheduled with Java timer.");
	
        
    }

    class RemindTask extends TimerTask 
    {	long start = System.currentTimeMillis();
	
    	    public void run() {
    	    
    	    long end = System.currentTimeMillis();
    	    System.out.println("ReminderTask is completed by Java timer: "+ (end-start));
    	    System.out.println("User power off VM!!!");
    	    
			VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
			if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn
				&& "Team18_VM_Ubuntu2".equals(vm.getName()))
			{
				Task task = null;
				try {
					task = vm.powerOffVM_Task();
				} catch (TaskInProgress e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidState e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RuntimeFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					task.waitForMe();
				} catch (InvalidProperty e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RuntimeFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("vm:" + vm.getName() + " powered off.");
			}	
            timer.cancel(); //Not necessary because we call System.exit
            //System.exit(0); //Stops the AWT thread (and everything else)
        }
    }

    /*public static void main(String args[]) {
    		
        System.out.println("Timer to turn off VM is about to start");
        JavaReminder reminderBeep = new JavaReminder(5);
        
        System.out.println("Remindertask is scheduled with Java timer.");
    }
    */
}

