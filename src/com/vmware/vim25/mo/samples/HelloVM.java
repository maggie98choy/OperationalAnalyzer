/*================================================================================
Copyright (c) 2008 VMware, Inc. All Rights Reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

* Neither the name of VMware, Inc. nor the names of its contributors may be used
to endorse or promote products derived from this software without specific prior 
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL VMWARE, INC. OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
================================================================================*/

package com.vmware.vim25.mo.samples;


import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Timer;
import java.util.TimerTask;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

public class HelloVM 
{
	/**
	 * @param args
	 * @throws Exception
	 */   
	 Timer timer;
	 final static AtomicBoolean b_powerOff = new AtomicBoolean();	

	 public HelloVM()
	 {
		 
	 }
	 
	 public HelloVM(int seconds) 
	 {
	        timer = new Timer();  //At this line a new Thread will be created
	        timer.schedule(new RemindTask(), seconds*1000); //delay in milliseconds
	        System.out.println("Alarm clock to turn off VM is starting....");
	         
	 }

	    class RemindTask extends TimerTask 
	    {
	        @Override
	        public void run() 
	        {
	            timer.cancel(); //Not necessary because we call System.exit
	            b_powerOff.set(true);
	            //System.exit(0); //Stops the AWT thread (and everything else)
	        }
	    }
	public static void main(String[] args)  throws Exception 
	{
		
		long start = System.currentTimeMillis();
		URL url = new URL("https://130.65.133.64/sdk");
		final ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);
		
		String vmName = "Team18_VM_Ubuntu";
		String newHostName = "130.65.133.66";
		
			final VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
			        rootFolder).searchManagedEntity(
			            "VirtualMachine", vmName);
			
			//ManagedEntity[] mesh = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
			
			/*int y = mesh.length;
			System.out.println("no. of vhost: " + y);
			
			for(int a=0;a<y;a++)
			{
				final HostSystem vhost = (HostSystem) mesh[a];
				String vhName = vhost.getName();
				System.out.println("vhost name: " + vhName);
			}
			*/
		
			final HostSystem newHost = (HostSystem) new InventoryNavigator(
			        rootFolder).searchManagedEntity(
			            "HostSystem", newHostName);
			b_powerOff.set(false);
			
		if (vm != null)
		{
				
			System.out.println("vm name: " + vm.getName());
			//setup alarm clock to turn off VM 1 hour later
			HelloVM reminderBeep = new HelloVM(60*60);
				
			//if (newHost.toString() != null)
			//{
				System.out.println("vhost name: " + newHost.getName());
				
				ManagedEntityStatus hme = newHost.getOverallStatus();
				System.out.println("vhost status: " + hme.toString());
						
					//if (hme.toString().equalsIgnoreCase("green"))
					//{
				
						VirtualMachineConfigInfo vminfo = vm.getConfig();
						VirtualMachineCapability vmc = vm.getCapability();
					
						   System.out.println("VM Summary:" + vm.getSummary().toString());
						 
						/*if(value instanceof VirtualMachineQuickStats) 
			               {
			                  VirtualMachineQuickStats vmqs = (VirtualMachineQuickStats)value;
			                  String cpu = vmqs.getOverallCpuUsage()==null ? "unavailable" : vmqs.getOverallCpuUsage().toString();
			                  String memory = vmqs.getHostMemoryUsage()==null ? "unavailable" : vmqs.getHostMemoryUsage().toString();
			                  System.out.println("   Guest Status: " + vmqs.getGuestHeartbeatStatus().toString());
			                  System.out.println("   CPU Load %: " + cpu);
			                  System.out.println("   Memory Load %: " + memory);
			               }
						*/
						
						   //String[][] typeInfo = { new String[]{"VirtualMachine", "name","runtime"}};
							
							PropertySpec propertySpec = new PropertySpec();
							propertySpec.setAll(new Boolean(true));
							//propertySpec.setPathSet(new String[] { "name", "guest.guestState" });
							//propertySpec.setPathSet();
							propertySpec.setType("VirtualMachine");
							PropertySpec[] pSpecs = new PropertySpec[] {propertySpec};

							
						   //PropertySpec[] pSpecs = PropertyCollectorUtil.buildPropertySpecArray(typeInfo);
						   
							ObjectSpec[] oSpecs = createObjectSpecs(vm);
							PropertyFilterSpec pSpec = new PropertyFilterSpec();
							pSpec.setPropSet(pSpecs);
							pSpec.setObjectSet(oSpecs);

							final PropertyCollector pc = si.getPropertyCollector();
							PropertyFilter pf = pc.createFilter(pSpec, false);
						
						System.out.println("Print VM name: " +vm.getName());			
						System.out.println("Print VM config: " + vminfo.hashCode());
						System.out.println("Print VM CPU: " +vmc.toString());
						//System.out.println("Print vmName " +(Network)vm.getNetworks()[]);
						vm.getResourcePool();
						GuestInfo guestInfo = vm.getGuest();
						
						if (guestInfo!= null)
						{			
							System.out.println("Print VM guestinfo: " +guestInfo);
							System.out.println("Heartbeat status: "+ (ManagedEntityStatus)vm.getGuestHeartbeatStatus());
							
							//Capture snapshot of VM
							//vmc.multipleSnapshotsSupported = false;
							//System.out.println("Taking snapshot of the VM......");					
							//vm.createSnapshot_Task("snap1", "snapshot for creating linked virtual machines", 
							//	    false, true);
							
							
							//Thread to monitor VM's heartbeat, initiate failover process if necessary, 
							//                                       take snapshot of VM every 20 mins 
							
							new Thread
							(
								new Runnable()
								{
									boolean b_stop = false;
									int counter = 0;
									
									public void run()
									{
								
										while(!b_stop)
										{
											for(;;)
											{
												try 
												{
													Thread.sleep(5000);
												} 
												catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												String version ="";
												UpdateSet update = null;
												try {
													update = pc.checkForUpdates(version);
												} catch (InvalidCollectorVersion e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
												} catch (RuntimeFault e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
												} catch (RemoteException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
												}
												if(update != null && update.getFilterSet() != null) 
												{
													handleUpdate(update);
													version = update.getVersion();
													System.out.println("version is:" + version);
												} 
												else
												{
													System.out.println("No update is present!");
												}
												
												ManagedEntityStatus me = vm.getGuestHeartbeatStatus();				
												System.out.println("Heartbeat status: " +me);
												
												if (b_powerOff.get())
												{
													  Task task = null;
														try 
														{
														    System.out.println("Alarm clock to turn off VM is completed by Java timer");
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
															if(task.waitForMe()==Task.SUCCESS)
															  {
															    System.out.println(vm.getName() + " powered off");
															    si.getServerConnection().logout();
															    System.exit(1);
															    
															  }
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
												}	
											
												//if (!me.toString().equals("green"))
												//{
													if (!b_powerOff.get())
													{
														//trigger clone process	
														System.out.println("Heartbeat fail!!!!");
													    System.out.println("Launching the VM clone task. " +
											    		"Please wait ...");
													    
														/*try 
														{
															//cloneVM(vm,newHost);
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
														*/
														b_stop = true;
														si.getServerConnection().logout();
														System.exit(1);
													
													//}
													
												}
												
												counter++;
												
												//Take snapshot of vm every 20 mins
												//use counter to keep track of # of cycle of checking heartbeat every 5s
												//20mins = (12X5s)X2
												//       = 24 cycles of 5 mins
												if (counter==24)
												{
													counter = 0;
													System.out.println("Taking snapshot of the vm......");					
													try {
														vm.createSnapshot_Task("snap1", "snapshot for creating linked virtual machines", 
															    false, true);
													} catch (InvalidName e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													} catch (VmConfigFault e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													} catch (SnapshotFault e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													} catch (TaskInProgress e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													} catch (FileFault e) {
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
													
												}
											}
										}
									}
								}
							).run();
						}
					//}
					//else
					//{
					//	System.out.println("Destinated host is not available");
					//}
			//}
			//else
			//{
			//	System.out.println("Destinated host is not found!!!");
			//}
		}
		else
		{
			System.out.println("Targeted VM is not found!!!");					
		}
		
		si.getServerConnection().logout();

	}
	
	
	//public static void cloneVM(VirtualMachine vm, HostSystem vhost, ComputeResource cr) throws InvalidProperty, RuntimeFault, RemoteException
	public static void cloneVM(VirtualMachine vm, HostSystem vhost) throws InvalidProperty, RuntimeFault, RemoteException
	{
		 VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
		 VirtualMachineRelocateSpec relocateSpec = new VirtualMachineRelocateSpec();
		 
		 ComputeResource crr = (ComputeResource) vhost.getParent();
		 relocateSpec.setHost(vhost.getMOR()); 	
		 relocateSpec.setPool(crr.getResourcePool().getMOR());
		 
		 cloneSpec.setLocation(relocateSpec);
		 cloneSpec.setPowerOn(true);
		 cloneSpec.setTemplate(false);
		 cloneSpec.snapshot = vm.getSnapshot().currentSnapshot;
		 
		 Task task = null;
		try {
			task = vm.cloneVM_Task((Folder) vm.getParent(), 
				     "Team18_VM_Ubuntu2_clone1", cloneSpec);
			
		} catch (VmConfigFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TaskInProgress e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CustomizationFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidState e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InsufficientResourcesFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MigrationFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDatastore e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			    String status = task.waitForMe();
			    if(status==Task.SUCCESS)
			    {
			      System.out.println("VM got cloned successfully.");
			    }
			    else
			    {
			      System.out.println("Failure -: VM cannot be cloned");
			 
			    }
	}

	
	public static ComputeResource getComputeResourceObject(HostSystem hs) 
	{
		
		/*ClusterComputeResource ccr= null;
		HostConnectSpec spec =null ;
		spec.hostName = hs.getName();
		spec.userName = "administrator";
		spec.password = "121@qwQW";
		ComputeResource cr;
		cr = (ComputeResource) hs.getParent();
		ccr.addHost_Task(spec, true,(ResourcePool)cr.getResourcePool());
		boolean found = false;
		
		for(HostSystem h:ccr.getHosts())
		{
			if (hs.getName().equalsIgnoreCase(h.getName()))
			{
				cr = ccr.
				found = true;
			}
		}
		
		return ccr;
		
		*/
		
		
		ComputeResource cr = null;
		int MAX_ACTIVE_COMPUTERESOURCE_OBJ = 10;		
		ArrayList<ComputeResource> crobj = new ArrayList<ComputeResource>();	
		boolean found =false;
		
		for (ComputeResource cre: crobj)	
		{
			if (cre.getName().equalsIgnoreCase(hs.getName()))
			{
				cr = cre;
				found = true;
				break;
			}
		}
		
		//host is not found in active object list
		if (!found)
		{
			if (crobj.size()>MAX_ACTIVE_COMPUTERESOURCE_OBJ)
			{
				Random randomizer = new Random();
		  		int r = 0;
				r = randomizer.nextInt(MAX_ACTIVE_COMPUTERESOURCE_OBJ);
		  		crobj.remove(r);
		  	
				cr = (ComputeResource) hs.getParent();
				cr.getResourcePool();
				crobj.add(cr);
			}
		}
		
		return cr;
		
		
	}
	
	static ObjectSpec[] createObjectSpecs(ManagedObject mo)
	{
		ObjectSpec[] oSpecs = new ObjectSpec[] { new ObjectSpec() };            
		oSpecs[0].setObj(mo.getMOR());
		oSpecs[0].setSkip(Boolean.FALSE);
		//oSpecs[0].setSelectSet(PropertyCollectorUtil.buildFullTraversal()); // in doubt here...
		return oSpecs;
	}
	
	static void handleUpdate(UpdateSet update) 
	   {
	      ArrayList vmUpdates = new ArrayList();
	      ArrayList hostUpdates = new ArrayList();
	      PropertyFilterUpdate[] pfus = update.getFilterSet(); 
	      for(int i=0; i<pfus.length; i++) 
	      {
	         ObjectUpdate[] ous = pfus[i].getObjectSet();
	         for(int j=0; j<ous.length; ++j) 
	         {
	            if(ous[j].getObj().getType().equals("VirtualMachine")) 
	            {
	               vmUpdates.add(ous[j]);
	            } 
	            else if(ous[j].getObj().getType().equals("HostSystem")) 
	            {
	               hostUpdates.add(ous[j]);
	            }
	         }
	      }      
	      if(vmUpdates.size() > 0) 
	      {
	         System.out.println("Virtual Machine updates:");
	         for(Iterator vmi = vmUpdates.iterator(); vmi.hasNext();) 
	         {
	            handleObjectUpdate((ObjectUpdate)vmi.next());
	         }
	      }      
	      if(hostUpdates.size() > 0) 
	      {
	         System.out.println("Host updates:");
	         for(Iterator vmi = hostUpdates.iterator(); vmi.hasNext();) 
	         {
	            handleObjectUpdate((ObjectUpdate)vmi.next());
	         }
	      }
	   }
	
	 static void handleObjectUpdate(ObjectUpdate oUpdate) 
	   {
	      PropertyChange[] pc = oUpdate.getChangeSet();
	      System.out.println(oUpdate.getKind() + "Data:");
		  handleChanges(pc);
	   }   
	   
	   static void handleChanges(PropertyChange[] changes) 
	   {
	      for(int i=0; i < changes.length; i++) 
	      {
	         String name = changes[i].getName();
	         Object value = changes[i].getVal();
	         PropertyChangeOp op = changes[i].getOp();
	         if(op != PropertyChangeOp.remove)
	         {
	        	 System.out.println("  Property Name: " + name);
	        	 if("summary.quickStats".equals(name)) 
	        	 {               
	               if(value instanceof VirtualMachineQuickStats) 
	               {
	                  VirtualMachineQuickStats vmqs = (VirtualMachineQuickStats)value;
	                  String cpu = vmqs.getOverallCpuUsage()==null ? "unavailable" : vmqs.getOverallCpuUsage().toString();
	                  String memory = vmqs.getHostMemoryUsage()==null ? "unavailable" : vmqs.getHostMemoryUsage().toString();
	                  System.out.println("   Guest Status: " + vmqs.getGuestHeartbeatStatus().toString());
	                  System.out.println("   CPU Load %: " + cpu);
	                  System.out.println("   Memory Load %: " + memory);
	                 
	               } 
	               else if (value instanceof HostListSummaryQuickStats) 
	               {
	                  HostListSummaryQuickStats hsqs = (HostListSummaryQuickStats)value;
	                  String cpu = hsqs.getOverallCpuUsage()==null ? "unavailable" : hsqs.getOverallCpuUsage().toString();
	                  String memory = hsqs.getOverallMemoryUsage()==null ? "unavailable" : hsqs.getOverallMemoryUsage().toString();
	                  System.out.println("   CPU Load %: " + cpu);
	                  System.out.println("   Memory Load %: " + memory);
	                
	                
	               }
	            } 
	            else if("runtime".equals(name)) 
	            {
	               if(value instanceof VirtualMachineRuntimeInfo) 
	               {
	                  VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo)value;
	                  System.out.println("   Power State: " + vmri.getPowerState().toString());
	                  System.out.println("   Connection State: " + vmri.getConnectionState().toString());
	                  Calendar bTime = vmri.getBootTime();
	                  if(bTime != null) 
	                  {
	                     System.out.println("   Boot Time: " + bTime.getTime());
	                  }
	                  Long mOverhead = vmri.getMemoryOverhead();
	                  if(mOverhead != null) 
	                  {
	                     System.out.println("   Memory Overhead: "+mOverhead);
	                  }
	               } 
	               else if(value instanceof HostRuntimeInfo) 
	               {
	                  HostRuntimeInfo hri = (HostRuntimeInfo)value;
	                  System.out.println("   Connection State: " + hri.getConnectionState().toString());
	                  
	                  Calendar bTime = hri.getBootTime();
	               
	                  if(bTime != null) 
	                  {
	                     System.out.println("   Boot Time: " + bTime.getTime());
	                  }
	               }
	            } 
	            else if("name".equals(name)) 
	            {
	               System.out.println("   "+value);
	            }
	           
	         } 
	         else 
	         {
	            System.out.println("Property Name: " +name+ " value removed.");
	         }
	      }
	   }

	
}

