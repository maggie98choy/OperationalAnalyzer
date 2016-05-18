import java.net.URL;

import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRelocateDiskMoveOptions;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class CloneFromSnapshot 
{
	public static void main(String[] args) throws Exception
	{
		
		long start = System.currentTimeMillis();
		URL url = new URL("https://130.65.133.60/sdk");
		ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		if(mes==null || mes.length ==0)
		{
			return;
		}
		
		//Total no. of vms
		int x = mes.length;
		
		System.out.println("Length: " + x);
		
		for (int i=0;i<x;i++)
		{
			VirtualMachine vm = (VirtualMachine) mes[i]; 
			String vmName = vm.getName();
			
			if (vmName.equalsIgnoreCase("Team18_VM_Ubuntu2"))
			{
				VirtualMachineConfigInfo vminfo = vm.getConfig();
				VirtualMachineCapability vmc = vm.getCapability();

				vm.getResourcePool();
				System.out.println("Hello " + vm.getName());
		
				GuestInfo guestInfo = vm.getGuest();
				if (guestInfo!= null)
				{
					System.out.println("Print guestinfo " +guestInfo);
					vm.createSnapshot_Task("snap1", "snapshot for creating linked virtual machines", 
							    false, true);
										
					 VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
					 VirtualMachineRelocateSpec relocateSpec = new VirtualMachineRelocateSpec();
					 
					 //relocateSpec.host =""; 
					 
					 cloneSpec.setLocation(relocateSpec);
					 cloneSpec.setPowerOn(false);
					 cloneSpec.setTemplate(false);
					 cloneSpec.snapshot = vm.getSnapshot().currentSnapshot;
					 
					 Task task = vm.cloneVM_Task((Folder) vm.getParent(), 
						     "Team18_VM_Ubuntu2_clone", cloneSpec);
						    System.out.println("Launching the VM clone task. " +
						    		"Please wait ...");

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
			
			}
		}
	}
}