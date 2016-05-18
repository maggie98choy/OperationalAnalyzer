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

package com.vmware.vim25.mo.samples.vm;

import java.net.URL;

import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * http://vijava.sf.net
 * @author Steve Jin
 */

public class CloneVM 
{
  public static void main(String[] args) throws Exception
  {
    /*if(args.length!=5)
    {
      System.out.println("Usage: java CloneVM <url> " +
      "<username> <password> <vmname> <clonename>");
      System.exit(0);
    }
   */
    //String vmname = args[3];
    //String cloneName = args[4];
	  String vmname = "Team18_VM_Ubuntu2";
	  String cloneName ="Team18_VM_Ubuntu2clone";
	  String newHostName = "130.65.133.63";
		
		
    ServiceInstance si = new ServiceInstance(
        new URL("https://130.65.133.60/sdk"), "administrator", "12!@qwQW", true);

    Folder rootFolder = si.getRootFolder();
    
    VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
        rootFolder).searchManagedEntity(
            "VirtualMachine", vmname);

	HostSystem newHost = (HostSystem) new InventoryNavigator(
	        rootFolder).searchManagedEntity(
	            "HostSystem", newHostName);

    if(vm==null)
    {
      System.out.println("No VM " + vmname + " found");
      si.getServerConnection().logout();
      return;
    }

        Task task = vm.createSnapshot_Task("snap1", "snapshot for creating linked virtual machines", 
	        	    false, true);

        if(task.waitForMe()==Task.SUCCESS)
        {
          System.out.println("Snapshot was created.");
        }
  
    VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
    VirtualMachineRelocateSpec relSpec = new VirtualMachineRelocateSpec();
    ComputeResource cr = (ComputeResource) newHost.getParent();
    
    relSpec.setHost(newHost.getMOR());
    relSpec.setPool(cr.getResourcePool().getMOR());
    cloneSpec.setLocation(relSpec);
    cloneSpec.setPowerOn(false);
    cloneSpec.setTemplate(false);
    cloneSpec.snapshot = vm.getSnapshot().currentSnapshot;
	

    Task task1 = vm.cloneVM_Task((Folder) vm.getParent(), 
        cloneName, cloneSpec);
    System.out.println("Launching the VM clone task. " +
    		"Please wait ...");

    @SuppressWarnings("deprecation")
	String status = task1.waitForMe();
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
