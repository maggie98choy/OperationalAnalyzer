package project2;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.HostCapability;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigSummary;
import com.vmware.vim25.HostHardwareSummary;
import com.vmware.vim25.HostIpConfig;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostListSummaryQuickStats;
import com.vmware.vim25.HostRuntimeInfo;
import com.vmware.vim25.HostSystemResourceInfo;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.ObjectUpdate;
import com.vmware.vim25.PropertyChange;
import com.vmware.vim25.PropertyChangeOp;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertyFilterUpdate;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.ResourceConfigSpec;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SharesInfo;
import com.vmware.vim25.UpdateSet;
import com.vmware.vim25.VirtualMachineQuickStats;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ManagedObject;
import com.vmware.vim25.mo.PropertyCollector;
import com.vmware.vim25.mo.PropertyFilter;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

//import project2.DBConnection;


public class LogCollection 
{
	
	private MongoClient mongoClient;
	private static DB db;
	static String json;
	DBObject dbObject;
	ManagedEntity[] mevh;
	
	public LogCollection() throws UnknownHostException
	{
		//mongoClient = new MongoClient("130.65.157.100",27017);
		mongoClient = new MongoClient("54.245.7.205",27017);
		db = mongoClient.getDB("283Project2");
		
		long start = System.currentTimeMillis();
		URL url;
		try {
			url = new URL("https://130.65.133.64/sdk");
		
			final ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);
			long end = System.currentTimeMillis();
			System.out.println("time taken:" + (end-start));
			Folder rootFolder = si.getRootFolder();
			mevh = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	
	public void getSystemData () throws Exception
	{
		long start = System.currentTimeMillis();
		URL url = new URL("https://130.65.133.64/sdk");
		final ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);
		long end = System.currentTimeMillis();
		System.out.println("time taken:" + (end-start));
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);

		
		//String vmName = "Team18_VM_Ubuntu";
		//String hostName = "130.65.133.65";
		
		
			/*final VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
			        rootFolder).searchManagedEntity(
			            "VirtualMachine", vmName);
			*/
		
			/*final HostSystem vm = (HostSystem) new InventoryNavigator(
					rootFolder).searchManagedEntity(
					"HostSystem", hostName);
			 */
			
			ManagedEntity[] mevh = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");		
			HostSystem vh; 
			PropertySpec propertySpec = new PropertySpec();
			ObjectSpec[] oSpecs = null;
			PropertyFilterSpec pSpec = new PropertyFilterSpec();
			PropertySpec[] pSpecs;
			PropertyCollector pc;
			PropertyFilter pf;
			UpdateSet update = null;			
			String version = "";			
			
			VirtualMachine[] vMachine;
			VirtualMachine vm;
			boolean bcontinue= true;
			
			do 
			{
				for(int a=0;a<mevh.length;a++)
				{
					vh = (HostSystem) mevh[a];
					String vhName = vh.getName();
					json = "{'host':'" + vhName + "'";
					System.out.println(json);
					System.out.println("");
					System.out.println("=====================================");
					System.out.println("$$$$$$$$$$   VH Name: " + vhName);
					
					propertySpec.setAll(new Boolean(true));
					propertySpec.setType("HostSystem");
					pSpecs = new PropertySpec[] {propertySpec};
					oSpecs = createObjectSpecs(vh);
					pSpec.setPropSet(pSpecs);
					pSpec.setObjectSet(oSpecs);
	
					pc = si.getPropertyCollector();
					pf = pc.createFilter(pSpec, false);
					
					version ="";				
					update = pc.checkForUpdates(version);
					
					if(update != null && update.getFilterSet() != null) 
					{
						handleUpdate(update, vhName);
						version = update.getVersion();
						System.out.println("version is:" + version);
					} 
					else
					{
						System.out.println("No update is present!");
					}
					
				    //store host summary into mongodb
					json = json + ", 'timestamp' :'" + new Date() +"'}";
					System.out.println(json);
					DBCollection HostCollection = db.getCollection("Host");
					dbObject = (DBObject)JSON.parse(json);					 
					HostCollection.insert(dbObject);
					
					//get all VMs hosted in the Vhost
					vMachine = vh.getVms();
					
					for (int b=0; b<vMachine.length; b++)
					{
						vm = vMachine[b];
						json = "{'vm':'" + vm.getName() + "'";
						System.out.println(json);
						propertySpec.setAll(new Boolean(true));
						propertySpec.setType("VirtualMachine");
						pSpecs = new PropertySpec[] {propertySpec};
						oSpecs = createObjectSpecs(vm);
						pSpec.setPropSet(pSpecs);
						pSpec.setObjectSet(oSpecs);
						
						System.out.println("");
						System.out.println("========================================");
						System.out.println("###################   VM Name: " + vm);
				
						pc = si.getPropertyCollector();
						pf = pc.createFilter(pSpec, false);
						
						version ="";				
						update = pc.checkForUpdates(version);
						Thread.sleep(5000);
						
						if(update != null && update.getFilterSet() != null) 
						{
							handleUpdate(update, vm.getName());
							version = update.getVersion();
							System.out.println("version is:" + version);
						} 
						else
						{
							System.out.println("No update is present!");
						}
						
					    //store VM summary into mongodb
						json = json + ", 'timestamp' :'" + new Date() +"'}";
						System.out.println(json);
						DBCollection VMcollection = db.getCollection("VM");
						dbObject = (DBObject)JSON.parse(json);					 
						VMcollection.insert(dbObject);
					
					}
					//Thread.sleep(5000);
				}
			} while(bcontinue);
	}
	
	static ObjectSpec[] createObjectSpecs(ManagedObject mo)
	{
		ObjectSpec[] oSpecs = new ObjectSpec[] { new ObjectSpec() };            
		oSpecs[0].setObj(mo.getMOR());
		oSpecs[0].setSkip(Boolean.FALSE);
		//oSpecs[0].setSelectSet(PropertyCollectorUtil.buildFullTraversal()); // in doubt here...
		return oSpecs;
	}
	
	static void handleUpdate(UpdateSet update, String vmname) 
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
	            handleObjectUpdate((ObjectUpdate)vmi.next(), vmname);
	         }
	      }      
	      if(hostUpdates.size() > 0) 
	      {
	         System.out.println("Host updates:");
	         for(Iterator vmi = hostUpdates.iterator(); vmi.hasNext();) 
	         {
	            handleObjectUpdate((ObjectUpdate)vmi.next(),vmname);
	         }
	      }
	   }
	
	 static void handleObjectUpdate(ObjectUpdate oUpdate, String vmname) 
	   {
	      PropertyChange[] pc = oUpdate.getChangeSet();
	      System.out.println(oUpdate.getKind() + "Data:");
		  handleChanges(pc, vmname);
	   }   
	   
	   static void handleChanges(PropertyChange[] changes, String vmname) 
	   {
		   
	      for(int i=0; i < changes.length; i++) 
	      {
	         String name = changes[i].getName();
	         Object value = changes[i].getVal();
	         PropertyChangeOp op = changes[i].getOp();
	         if(op != PropertyChangeOp.remove)
	         {
	        	 System.out.println("Property Name: " + name);
	        	 
	        	 if("summary".equals(name)) 
	        	 {               
	               if(value instanceof VirtualMachineSummary) 
	               {	
	            	  VirtualMachineSummary vms = (VirtualMachineSummary)value;
	                  VirtualMachineQuickStats vmqs = (VirtualMachineQuickStats)vms.getQuickStats();
	                 
	                  /*String cpu = vmqs.getOverallCpuUsage()==null ? "unavailable" : vmqs.getOverallCpuUsage().toString();
	                  String memory = vmqs.getHostMemoryUsage()==null ? "unavailable" : vmqs.getHostMemoryUsage().toString();
	                  String BallonedMemory = vmqs.getBalloonedMemory()==null ? "unavailable" : vmqs.getBalloonedMemory().toString();
	                  String CompressMemory = vmqs.getCompressedMemory()==null ? "unavailable" : vmqs.getCompressedMemory().toString();
	                  String ConsumedOverheadMemory = vmqs.getConsumedOverheadMemory()==null ? "unavailable" : vmqs.getConsumedOverheadMemory().toString();
	                  String DistributedCpuEntitlement = vmqs.getDistributedCpuEntitlement()==null ? "unavailable" : vmqs.getDistributedCpuEntitlement().toString();
	                  String DistributedMemoryEntitlement = vmqs.getDistributedMemoryEntitlement()==null ? "unavailable" : vmqs.getDistributedMemoryEntitlement().toString();
	                  String FtLogBandwidth = vmqs.getFtLogBandwidth()==null ? "unavailable" : vmqs.getFtLogBandwidth().toString();
	                  String FtSecondaryLatency = vmqs.getFtSecondaryLatency()==null ? "unavailable" : vmqs.getFtSecondaryLatency().toString();
	                  String GuestMemoryUsage = vmqs.getGuestMemoryUsage()==null ? "unavailable" : vmqs.getGuestMemoryUsage().toString();
	                  String HostMemoryUsage = vmqs.getHostMemoryUsage()==null ? "unavailable" : vmqs.getHostMemoryUsage().toString();
	                  String OverallCpuDemand = vmqs.getOverallCpuDemand()==null ? "unavailable" : vmqs.getOverallCpuDemand().toString();
	                  String OverallCpuUsage = vmqs.getOverallCpuUsage()==null ? "unavailable" : vmqs.getOverallCpuUsage().toString();
	                  String PrivateMemory = vmqs.getPrivateMemory()==null ? "unavailable" : vmqs.getPrivateMemory().toString();
	                  String SharedMemory = vmqs.getSharedMemory()==null ? "unavailable" : vmqs.getSharedMemory().toString();
	                  String StaticCpuEntitlement = vmqs.getStaticCpuEntitlement()==null ? "unavailable" : vmqs.getStaticCpuEntitlement().toString();
	                  String StaticMemoryEntitlement = vmqs.getStaticMemoryEntitlement()==null ? "unavailable" : vmqs.getStaticMemoryEntitlement().toString();
	                  String SwappedMemory = vmqs.getSwappedMemory()==null ? "unavailable" : vmqs.getSwappedMemory().toString();
	                  String UptimeSeconds = vmqs.getUptimeSeconds()==null ? "unavailable" : vmqs.getUptimeSeconds().toString();
	                  String GuestHeartbeatStatus = vmqs.getGuestHeartbeatStatus()==null ? "unavailable" : vmqs.getGuestHeartbeatStatus().toString();
	                  String FtLatencyStatus = vmqs.getFtLatencyStatus()==null ? "unavailable" : vmqs.getFtLatencyStatus().toString();
	                  
	                  System.out.println(" Guest Status: " + vmqs.getGuestHeartbeatStatus().toString());
	                  System.out.println(" CPU Load %: " + cpu);
	                  System.out.println(" Memory Load %: " + memory);   
	                  System.out.println(" Balloned Memory: "+ BallonedMemory);
	 	              System.out.println(" Compressed Memory: "+ CompressMemory);
	 	              System.out.println(" Consumed Overhead Memory: "+ ConsumedOverheadMemory);
	 	              System.out.println(" Distributed Cpu Entitlement: "+ DistributedCpuEntitlement);
	 	              System.out.println(" Distributed Memory Entitlement: "+ DistributedMemoryEntitlement);
	 	              System.out.println(" Ft Log Bandwidth: "+ FtLogBandwidth);
	 	              System.out.println(" Ft Secondary Latency: "+ FtSecondaryLatency);
	 	              System.out.println(" Guest Memory Usage: "+ GuestMemoryUsage);
	 	              System.out.println(" Host Memory Usage: "+ HostMemoryUsage);
	 	              System.out.println(" Overall Cpu Demand: "+ OverallCpuDemand);
	 	              System.out.println(" Overall Cpu Usage: "+ OverallCpuUsage);
	 	              System.out.println(" Private Memory: "+ PrivateMemory);
	 	              System.out.println(" Shared Memory: "+ SharedMemory);
	 	              System.out.println(" Static Cpu Entitlement: "+ StaticCpuEntitlement);
	 	              System.out.println(" Static Memory Entitlement: "+ StaticMemoryEntitlement);
	 	              System.out.println(" Swapped Memory: "+ SwappedMemory);
	 	              System.out.println(" Up time seconds: "+ UptimeSeconds);
	 	              System.out.println(" Heartbeat status: "+ GuestHeartbeatStatus);
	 	              System.out.println(" Ft Latency status: "+ FtLatencyStatus);
	                   */
	          	   json = json + ", 'VirtualMachineSummary': {'GuestHeartbeatStatus' :'" + vmqs.getGuestHeartbeatStatus() + "', 'OverallCpuUsage' :'" + vmqs.getOverallCpuUsage() + 
      			         "', 'HostMemoryUsage' :'" + vmqs.getHostMemoryUsage() + "', 'BalloonedMemory' :'" + vmqs.getBalloonedMemory() + "', 'CompressedMemory' :'" + vmqs.getCompressedMemory() +
      			         "', 'ConsumedOverheadMemory' :'" + vmqs.getConsumedOverheadMemory()+ "', 'DistributedCpuEntitlement' :'" + vmqs.getDistributedCpuEntitlement() + "', 'FtLogBandwidth' :'" + vmqs.getFtLogBandwidth() + 
      			         "', 'FtSecondaryLatency' :'" + vmqs.getFtSecondaryLatency() + "', 'GuestMemoryUsage' :'" + vmqs.getGuestMemoryUsage() + "', 'HostMemoryUsage' :'" + vmqs.getHostMemoryUsage() + 
      			         "', 'OverallCpuDemand' :'" + vmqs.getOverallCpuDemand() + "', 'OverallCpuUsage' :'" + vmqs.getOverallCpuUsage()+ "', 'PrivateMemory' :'" + vmqs.getPrivateMemory() +
	 	      	         "', 'SharedMemory' :'" + vmqs.getSharedMemory() + "', 'StaticCpuEntitlement' :'" + vmqs.getStaticCpuEntitlement()+ "', 'StaticMemoryEntitlement' :'" + vmqs.getStaticMemoryEntitlement() +
	 	      	         "', 'SwappedMemory' :'" + vmqs.getSwappedMemory()+ "', 'UptimeSeconds' :'" + vmqs.getUptimeSeconds() + "', 'GuestHeartbeatStatus' :'" + vmqs.getGuestHeartbeatStatus() + 
	 	      	         "', 'FtLatencyStatus' :'" + vmqs.getFtLatencyStatus() + "'}";
                   System.out.println("   json:" + json);
    	                  
	               } 
	               else if (value instanceof HostListSummaryQuickStats) 
	            	   
	               {
	                  HostListSummaryQuickStats hsqs = (HostListSummaryQuickStats)value;
	                  String cpu = hsqs.getOverallCpuUsage()==null ? "unavailable" : hsqs.getOverallCpuUsage().toString();
	                  String memory = hsqs.getOverallMemoryUsage()==null ? "unavailable" : hsqs.getOverallMemoryUsage().toString();
	                  System.out.println(" CPU Load %: " + cpu);
	                  System.out.println(" Memory Load %: " + memory);
	                  
	                  json = json + ", 'HostListSummaryQuickStats': {'OverallCpuUsage' :'" + cpu + "','OverallMemoryUsage' :'" + memory +"'}";
	                  System.out.println("   json:" + json);
	               }
	               else if (value instanceof HostListSummary)
	               {
	            	   HostListSummary hls = (HostListSummary) value;
	            	   HostConfigSummary hcs = (HostConfigSummary) hls.getConfig();
	            	   
	            	   /*String hname = hcs.getName()==null ? "unavailable" : hcs.getName().toString();
	            	   String SslThumbprint = hcs.getSslThumbprint()==null ? "unavailable" : hcs.getSslThumbprint().toString();
	            	   String FaultToleranceEnabled = hcs.getFaultToleranceEnabled()==null ? "unavailable" : hcs.getFaultToleranceEnabled().toString();
	            	   
	            	   System.out.println(" Name: " + hname);
	            	   System.out.println(" Port: " + hcs.getPort());
	            	   System.out.println(" Ssl Thumb print: " + SslThumbprint);
	            	   System.out.println(" Fault Tolerance Enabled: " + FaultToleranceEnabled);
	                   */
	            	   
	            	   json = json + ", 'HostListSummary': {'SslThumbprint' :'" + hcs.getSslThumbprint().toString() + "', 'FaultToleranceEnabled' :'" + hcs.getFaultToleranceEnabled().toString() + "'}";
		               System.out.println("   json:" + json);
	            	   
	            	   HostHardwareSummary hhs = (HostHardwareSummary) hls.getHardware();
	            	   String CpuModel = hhs.getCpuModel()==null ? "unavailable" : hhs.getCpuModel().toString();
	            	   String Model = hhs.getModel()==null ? "unavailable" : hhs.getModel().toString();
	            	   
	            	   /*System.out.println(" Cpu Mhz: " + hhs.getCpuMhz());
	            	   System.out.println(" Cpu Model: " + CpuModel);
	            	   System.out.println(" Memory Size: " + hhs.getMemorySize());
	            	   System.out.println(" Model: " + Model);
	            	   System.out.println(" Cpu Cores: " + hhs.getNumCpuCores());
	            	   System.out.println(" Number of Cpu Packages: " + hhs.getNumCpuPkgs());
	            	   System.out.println(" Number of Cpu Threads: " + hhs.getNumCpuThreads());
	            	   System.out.println(" Number of HBAs: " + hhs.getNumHBAs());
	            	   System.out.println(" Number of Nics: " + hhs.getNumNics());
	            	   System.out.println(" Uuid: " + hhs.getUuid());
	            	   System.out.println(" Vendor: " + hhs.getVendor());
	            	   */
	            	   
	            	   json = json + ", 'HostHardwareSummary': {'CpuMhz' :'" + hhs.getCpuMhz() + "', 'CpuModel' :'" + hhs.getCpuModel() + 
	            			         "', 'MemorySize' :'" + hhs.getMemorySize() + "', 'Model' :'" + hhs.getModel() + "', 'NumCpuCores' :'" + hhs.getNumCpuCores() +
	            			         "', 'NumCpuPkgs' :'" + hhs.getNumCpuPkgs() + "', 'NumCpuThreads' :'" + hhs.getNumCpuThreads() + "', 'NumHBAs' :'" + hhs.getNumHBAs() + 
	            			         "', 'NumNics' :'" + hhs.getNumNics() + "', 'Uuid' :'" + hhs.getUuid() + "', 'Vendor' :'" + hhs.getVendor()+ "'}";
		               System.out.println("   json:" + json);
	             	   
	               }
	         } 
	            else if("runtime".equals(name)) 
	            {
	               if(value instanceof VirtualMachineRuntimeInfo) 
	               {
	                  VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo)value;
	                  System.out.println(" Power State: " + vmri.getPowerState().toString());
	                  System.out.println(" Connection State: " + vmri.getConnectionState().toString());
	                  //Calendar bTime = vmri.getBootTime();
	                  //System.out.println(" Boot Time: " + bTime.getTime());
	                  
	                  Long mOverhead = vmri.getMemoryOverhead();
	                  if(mOverhead != null) 
	                  {
	                     System.out.println(" Memory Overhead: "+mOverhead);
	                  }
	         
	                  json = json + ", 'VirtualMachineRuntimeInfo': {'PowerState' :'" + vmri.getPowerState() + "', 'ConnectionState' :'" + vmri.getConnectionState() +  
	                		        "', 'BootTime' :'" + vmri.getBootTime() + "', 'MemoryOverhead' :'" + mOverhead + "'}";
	                  System.out.println("   json:" + json);
	         
	               } 
	               else if(value instanceof HostRuntimeInfo) 
	               {
	                  HostRuntimeInfo hri = (HostRuntimeInfo)value;
	                  /*
	                  System.out.println(" Connection State: " + hri.getConnectionState().toString());
	                  System.out.println(" Health System Runtime: " + hri.getHealthSystemRuntime().toString());
	                  System.out.println(" Standby Mode: " + hri.getStandbyMode());
	                  System.out.println(" Tpm Pcr value: " + hri.getTpmPcrValues());
	                  */
	                  //Calendar bTime = hri.getBootTime();
	                  
	                  json = json + ", 'HostRuntimeInfo': {'ConnectionState' :'" + hri.getConnectionState() + "', 'HealthSystemRuntime' :'" + hri.getHealthSystemRuntime()+  
              		         "', 'StandbyMode' :'" + hri.getStandbyMode() + "', 'TpmPcrValues' :'" + hri.getTpmPcrValues() + "', 'BootTime' :'" + hri.getBootTime() + "'}";
	                  System.out.println("   json:" + json);
  
	               }
	            } 
	            else if("guest".equals(name)) 
	            {
	               GuestInfo gif = (GuestInfo) value;
	               /*System.out.println(" Guest family:"+gif.getGuestFamily());
	               System.out.println(" Guest Full Name:"+gif.getGuestFullName());
	               System.out.println(" Guest Id:"+gif.getGuestId());
	               System.out.println(" Host Name:"+gif.getHostName());
	               System.out.println(" IP Address:"+gif.getIpAddress());
	               System.out.println(" Tools Running Status:"+gif.getToolsRunningStatus());
	               System.out.println(" Tools Version:"+gif.getToolsVersion());
	               System.out.println(" Tools Version Status:"+gif.getToolsVersionStatus());
	               */
	               GuestNicInfo[] gnif = (GuestNicInfo []) gif.getNet();
	        	   //System.out.println(" Gnid length:"+gnif.length);
		               
	               /*for (int j=1; j<=gnif.length; j++)
	               {
	            	   System.out.println(" Mac Address:"+gnif[j-1].getMacAddress());
	               }
	               */
	        	   
	               json = json + ", 'GuestInfo': {'GuestFamily' :'" + gif.getGuestFamily() + "', 'GuestId' :'" + gif.getGuestId()+  
            		         "', 'HostName' :'" + gif.getHostName() + "', 'IpAddress' :'" + gif.getIpAddress() + "', 'ToolsRunningStatus' :'" + gif.getToolsRunningStatus() + 
            		         "', 'ToolsVersion' :'" + gif.getToolsVersion() + "', 'ToolsVersionStatus' :'" + gif.getToolsVersionStatus() + "'}";
                   System.out.println("   json:" + json);
	               
	            }
	            else if("capability".equals(name)) 
	            {
	            	if(value instanceof HostCapability) 
	            	{	
	            		HostCapability hc = (HostCapability) value;
		            /*
	            		System.out.println(" Max Running VMs: "+hc.getMaxRunningVMs());
		            	System.out.println(" Max Supported Vcpus: "+hc.getMaxSupportedVcpus());
		            	System.out.println(" Clone From Snapshot Supported: "+hc.getCloneFromSnapshotSupported());
		            	System.out.println(" Delta Disk Backings Supported: "+hc.getDeltaDiskBackingsSupported());
		            	System.out.println(" Ft Compatibility Issues: "+hc.getFtCompatibilityIssues());
		            	System.out.println(" Ft Supported: "+hc.getFtSupported());
		            	System.out.println(" Ipmi Supported: "+hc.getIpmiSupported());
		            	System.out.println(" Login By SSL Thumbprint Supported: "+hc.getLoginBySSLThumbprintSupported());
		            	System.out.println(" Per VM Network Traffic Shaping Supported: "+hc.getPerVMNetworkTrafficShapingSupported());
		            	System.out.println(" Record Replay Supported: "+hc.getRecordReplaySupported());
		            	System.out.println(" Storage IORM Supported: "+hc.getStorageIORMSupported());
		            	System.out.println(" Storage VMotion Supported: "+hc.getStorageVMotionSupported());
		            	System.out.println(" Supported Cpu Feature: "+hc.getSupportedCpuFeature());
		            	System.out.println(" Tpm Supported: "+hc.getTpmSupported());
		            	System.out.println(" Virtual Exec Usage Supported: "+hc.getVirtualExecUsageSupported());
		            	System.out.println(" Vm Direct Path Gen2 Supported: "+hc.getVmDirectPathGen2Supported());
		            	System.out.println(" Vmotion With Storage VMotion Supported: "+hc.getVmotionWithStorageVMotionSupported());
		            	System.out.println(" VStorage Capable: "+hc.getVStorageCapable());
		            */	
		                json = json + ", 'HostCapability': {'MaxRunningVMs' :'" + hc.getMaxRunningVMs() + "', 'MaxSupportedVcpus' :'" + hc.getMaxSupportedVcpus() +  
	            		         "', 'CloneFromSnapshotSupported' :'" + hc.getCloneFromSnapshotSupported() + "', 'DeltaDiskBackingsSupported' :'" + hc.getDeltaDiskBackingsSupported()+ "', 'FtCompatibilityIssues' :'" + hc.getFtCompatibilityIssues() + 
	            		         "', 'FtSupported' :'" + hc.getFtSupported()+ "', 'IpmiSupported' :'" + hc.getIpmiSupported() +
		                         "', 'LoginBySSLThumbprintSupported' :'" + hc.getLoginBySSLThumbprintSupported() + "', 'VMNetworkTrafficShapingSupported' :'" + hc.getPerVMNetworkTrafficShapingSupported() +
		                         "', 'RecordReplaySupported' :'" + hc.getRecordReplaySupported() + "', 'StorageIORMSupported' :'" + hc.getStorageIORMSupported() + 
		                         "', 'StorageVMotionSupported' :'" + hc.getStorageVMotionSupported() + "', 'SupportedCpuFeature' :'" + hc.getSupportedCpuFeature() + 
		                         "', 'TpmSupported' :'" + hc.getTpmSupported() + "', 'VirtualExecUsageSupported' :'" + hc.getVirtualExecUsageSupported() + 
		                		 "', 'VmDirectPathGen2Supported' :'" + hc.getVmDirectPathGen2Supported() + "', 'VmotionWithStorageVMotionSupported' :'" + hc.getVmotionWithStorageVMotionSupported() + 
		                		 "', 'VStorageCapable' :'" + hc.getVStorageCapable()+ "'}";
	                    System.out.println("   json:" + json);

	            	}
	            	
	            }
	            else if("overallStatus".equals(name)) 
	            {
	            	//HostHealthStatusSystem hhss = (HostHealthStatusSystem) value;
	            	ManagedEntityStatus mes = (ManagedEntityStatus) value;
	            	System.out.println(" overallStatus:" + mes.toString());
	                json = json + ", 'overallStatus': {'ManagedEntityStatus' :'" + mes.toString() + "'}";
	                System.out.println("   json:" + json);

	            } 
	        	 
	            else if ("systemResources".equals(name))
	            {
	            	if(value instanceof HostSystemResourceInfo) 
	            	{	
	            		HostSystemResourceInfo hsri = (HostSystemResourceInfo) value;
	            		ResourceConfigSpec rcs = (ResourceConfigSpec)hsri.getConfig();
	            		/*
	            		System.out.println(" Cpu Allocation limit: "+rcs.getCpuAllocation().getLimit());
	            		System.out.println(" Cpu Allocation reservation: "+rcs.getCpuAllocation().getReservation());
	            		System.out.println(" Cpu Allocation overhead limit: "+rcs.getCpuAllocation().getOverheadLimit());
	            		System.out.println(" Cpu Allocation Expandable Reservation: "+rcs.getCpuAllocation().getExpandableReservation());
	            		
	            		*/SharesInfo si = (SharesInfo) rcs.getCpuAllocation().getShares();
	            		System.out.println(" Cpu Allocation Shares: "+si.getShares());
	            		
	                    json = json + ", 'HostSystemResourceInfo': {'CpuAllocationlimit' :'" + rcs.getCpuAllocation().getLimit() + "', 'CpuAllocationreservation' :'" + rcs.getCpuAllocation().getReservation() +  
	            		         "', 'CpuAllocationOverheadlimit' :'" + rcs.getCpuAllocation().getOverheadLimit() + "', 'CpuAllocationExpandableReservation' :'" + rcs.getCpuAllocation().getExpandableReservation() + "', 'CpuAllocationShares' :'" + si.getShares() + "'}";
	                    System.out.println("   json:" + json);
		        		
	            	}
	            }
	            else if ("config".equals(name))
	            {
	            	if (value instanceof HostConfigInfo)
	            	{
	            		HostConfigInfo hci = (HostConfigInfo) value;
	            		//Ipconfig =null -> vmotion is disabled
	            		HostIpConfig s_IpConfig = hci.getVmotion().getIpConfig();
	            		if (s_IpConfig!= null)
	            		{
	            			/*
	            			System.out.println(" vMotion Ip config Ip Address:"+ hci.getVmotion().getIpConfig().getIpAddress());
	            			System.out.println(" vMotion Ip config V6 Config:"+ hci.getVmotion().getIpConfig().getIpV6Config());
	            			System.out.println(" vMotion Ip config Subnet Mask:"+ hci.getVmotion().getIpConfig().getSubnetMask());
	            			*/
	                        json = json + ", 'HostConfigInfo': {'vMotion' :'enabled" +
	                        		"', 'vMotionIpconfigIpAddress' :'" + hci.getVmotion().getIpConfig().getIpAddress() +
	                        		"', 'vMotionIpconfigV6Config' :'" + hci.getVmotion().getIpConfig().getIpV6Config() +  
		            		        "', 'vMotionIpconfigSubnetMask' :'" + hci.getVmotion().getIpConfig().getSubnetMask() + "'}";
		                    System.out.println("   json:" + json);
			
	            		}
	            		else
	            		{
	            			System.out.println(" vMotion: disabled");
	           		        json = json + ", 'HostConfigInfo': {'vMotion':'disabled'}"; 
			       		    System.out.println("   json:" + json);
				
	            		}
	            		
	            	}
	            }
	            else if ("declaredAlarmState".equals(name))
	            {
	     			System.out.println("declaredAlarmState");
	 	          
	            }
	            
	         } 
	         else 
	         {
	            System.out.println("Property Name: " +name+ " value removed.");
	         }
	      }
	      
	  
	   }
	   
	   public ManagedEntity[] getManagedEntity()
	   {
		   return (mevh);
	   }
	
	
}
