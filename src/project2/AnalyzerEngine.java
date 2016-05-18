package project2;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.VirtualMachine;

public class AnalyzerEngine {
	//mysql connection
	Connection con;
	//mongo client
	MongoClient mongoClient;
	//mongo DB
	DB db;
	//hostList
	private String hostList[] = null;
	//VM List
	private String vmList[] = null;
	
	public AnalyzerEngine() throws SQLException
	{
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//String url = "jdbc:mysql://54.245.7.205:3306/283Project2";
			//con = DriverManager.getConnection(url,"root", "12!@qwQW");
			/*String url = "jdbc:mysql://localhost:3306/mysql";
			con = DriverManager.getConnection(url,"root", "mysql");*/
			//String connectionString = "jdbc:mysql://54.245.7.205:3306/283Project2?user=aswathy&password=12!@qwQW&useUnicode=true&characterEncoding=UTF-8";
			String url = "jdbc:mysql://54.245.7.205:3306/283Project2";
			con = DriverManager.getConnection(url,"cmpe283","12!@qwQW");
			
			mongoClient = new MongoClient("54.245.7.205",27017);
			db = mongoClient.getDB("283Project2");
			//String hostList[]=null;
			
				LogCollection logCollector = new LogCollection();
				ManagedEntity mevh[] = logCollector.getManagedEntity();
				hostList = new String[mevh.length];
				
				for(int a=0;a<mevh.length;a++)
				{
					hostList[a] = ((HostSystem) mevh[a]).getName();
				}
			
			}  catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	} 
		
	
	
	protected void finalize()
	{
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void fiveMinReducer(Date date)
	{		
		//Reducing Host data
		//String hostList[] = {"130.65.133.65","130.65.133.66","130.65.133.67","130.65.133.68","130.65.133.69"};
		
		int hostNum = hostList.length;
		DBCollection hostCollection = db.getCollection("Host");
		//System.out.println(System.currentTimeMillis());
		BasicDBObject findQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		fields.put("host", 1);
		fields.put("VirtualMachineSummary", 1);
		fields.put("HostCapability", 1);
		fields.put("timestamp", 1);
		//findQuery.put("timestamp", "Mon Nov 25 16:23:21 PST 2013");
		//Date startTime = new Date((System.currentTimeMillis())-300000);
		//Date endTime = new Date();
		//Date endTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(inputDate);//"2013-11-25 16:23:21"
		//Date endTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(inputDate);
		Date endTime = date;
		Date startTime = new Date(endTime.getTime()-300000);
		
		//DateFormat formatter = new SimpleDateFormat("mm/DD/yyyy");
		//Date endTime = (Date)formatter.parse("11/26/2013");
		
		//System.out.println(endTime);
		//findQuery.put("timestamp", BasicDBObjectBuilder.start("$lte",endTime).get());//.add("$gt",startTime).get());
		//findQuery.put("timestamp", new BasicDBObject("$lte",endTime));
		//findQuery.put("timestamp", new BasicDBObject("$lte",new Date().toString()));
		findQuery.put("timestamp", BasicDBObjectBuilder.start("$lte",endTime.toString()).add("$gt",startTime.toString()).get());
		DBCursor cursor = hostCollection.find(findQuery,fields);//.sort(new BasicDBObject("timestamp",-1));
		String host=null,vm=null;
		/*int overallCpuUsage = 0,BalloonedMemory = 0,ConsumedOverheadMemory=0,FtLogBandwidth = 0,FtSecondaryLatency = 0,GuestMemoryUsage = 0,OverallCpuDemand = 0,
		PrivateMemory = 0,SharedMemory = 0,StaticCpuEntitlement = 0,StaticMemoryEntitlement = 0,SwappedMemory = 0,MaxRunningVMs = 0;*/
		/*int hostCount = cursor.count();
		System.out.println(hostCount);*/
		int overallCpuUsage[] = new int[hostNum]; 
		int BalloonedMemory[] = new int[hostNum];
		int ConsumedOverheadMemory[] = new int[hostNum];
		int FtLogBandwidth[] = new int[hostNum];
		int FtSecondaryLatency[] = new int[hostNum];
		int GuestMemoryUsage[] = new int[hostNum];
		int OverallCpuDemand[] = new int[hostNum];
		int PrivateMemory[] = new int[hostNum];
		int SharedMemory[] = new int[hostNum];
		int StaticCpuEntitlement[] = new int[hostNum];
		int StaticMemoryEntitlement[] = new int[hostNum];
		int SwappedMemory[] = new int[hostNum];
		int MaxRunningVMs[] = new int[hostNum];
		int vmSummaryCount[] = new int[hostNum];
		int HostCapabilityCount[ ]=new int[hostNum];

		while(cursor.hasNext()) {
			DBObject result = (DBObject)cursor.next(); 
			//System.out.println(result.toString());
			//System.out.println(result.get("VirtualMachineSummary"));
			 host = (String)result.get("host");
			 int hostID=0;
			 for(;hostID<hostNum;hostID++)
			 {
				 if(host.equals(hostList[hostID]))
					 break;
			 }
			BasicDBObject VMSummary =(BasicDBObject) result.get("VirtualMachineSummary");
			if(VMSummary!=null)
			{
				vmSummaryCount[hostID]++;
			//System.out.println(VMSummary.get("GuestHeartbeatStatus"));
			 //time = (String)VMSummary.get("timestamp");
			 overallCpuUsage[hostID] += (Integer.parseInt((String)VMSummary.get("OverallCpuUsage")));
			//System.out.println(overallCpuUsage);
			 BalloonedMemory[hostID]+=Integer.parseInt((String)VMSummary.get("BalloonedMemory"));
			 ConsumedOverheadMemory[hostID] += Integer.parseInt((String)VMSummary.get("ConsumedOverheadMemory"));
			 FtLogBandwidth[hostID] += Integer.parseInt((String)VMSummary.get("FtLogBandwidth"));
			 FtSecondaryLatency[hostID] += Integer.parseInt((String)VMSummary.get("FtSecondaryLatency"));
		     GuestMemoryUsage[hostID] += Integer.parseInt((String)VMSummary.get("GuestMemoryUsage"));
		     OverallCpuDemand[hostID] += Integer.parseInt((String)VMSummary.get("OverallCpuDemand"));
		     PrivateMemory[hostID] += Integer.parseInt((String)VMSummary.get("PrivateMemory"));
		     SharedMemory[hostID] += Integer.parseInt((String)VMSummary.get("SharedMemory"));
		     StaticCpuEntitlement[hostID] += Integer.parseInt((String)VMSummary.get("StaticCpuEntitlement"));
		     StaticMemoryEntitlement[hostID] += Integer.parseInt((String)VMSummary.get("StaticMemoryEntitlement"));
		     SwappedMemory[hostID] += Integer.parseInt((String)VMSummary.get("SwappedMemory"));
			}
			BasicDBObject HostCapability = (BasicDBObject) result.get("HostCapability");
			if(HostCapability!=null)
			{
				HostCapabilityCount[hostID]++;
				MaxRunningVMs[hostID] += Integer.parseInt((String)HostCapability.get("MaxRunningVMs"));
			}
		}
		//System.out.println(vmSummaryCount+" "+HostCapabilityCount);
		//System.out.println(MaxRunningVMs);
		//Calculating and storing average values into MySQL
		for(int j=0;j<hostNum;j++)
		{
			if(vmSummaryCount[j]>0)
			{
				overallCpuUsage[j]/=vmSummaryCount[j];
				BalloonedMemory[j]/= vmSummaryCount[j];
				ConsumedOverheadMemory[j]/=vmSummaryCount[j];
				FtLogBandwidth[j]/=vmSummaryCount[j];
				FtSecondaryLatency[j]/=vmSummaryCount[j];
				GuestMemoryUsage[j]/=vmSummaryCount[j];
				OverallCpuDemand[j]/=vmSummaryCount[j];
				PrivateMemory[j]/=vmSummaryCount[j];
				SharedMemory[j]/=vmSummaryCount[j];
				StaticCpuEntitlement[j]/=vmSummaryCount[j];
				StaticMemoryEntitlement[j]/=vmSummaryCount[j];
				SwappedMemory[j]/=vmSummaryCount[j];
			}
			if(HostCapabilityCount[j]>0)
				MaxRunningVMs[j]/=HostCapabilityCount[j];
			
			//System.out.println(MaxRunningVMs);
		
			try {
					Statement insertStmt = con.createStatement();
					String query = "Insert into Host_Stats(HostName,TimeStamp,OverallCpuUsage,BalloonedMemory,ConsumedOverheadMemory,FtLogBandwidth," +
							"FtSecondaryLatency,GuestMemoryUsage,OverallCpuDemand,PrivateMemory,SharedMemory,StaticCpuEntiltlement,StaticmemoryEntiltement,SwappedMemory,MaxRunningVMs) Values ('"+hostList[j]+"','"+endTime+"',"+overallCpuUsage[j]+","+BalloonedMemory[j]+
							","+ConsumedOverheadMemory[j]+","+FtLogBandwidth[j]+","+FtSecondaryLatency[j]+","+GuestMemoryUsage[j]+","+OverallCpuDemand[j]+","+PrivateMemory[j]+
							","+SharedMemory[j]+","+StaticCpuEntitlement[j]+","+StaticMemoryEntitlement[j]+","+SwappedMemory[j]+","+MaxRunningVMs[j]+")";
					//System.out.println(query);
					int updateResult = insertStmt.executeUpdate(query);
					//System.out.println("Number of Rows updated: "+updateResult);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//Reducing VM data
		String vmList[] = {"Team18_VM_Ubuntu","Team18_VM_Ubuntu1","Team18_VM_Ubuntu2","Team18_VM_Ubuntu3","Team18_VM_Ubuntu4","Team18_VM_Ubuntu5","Team18_VM_Ubuntu6","Team18_VM_Ubuntu7"};
		int vmNum = vmList.length;
		int vmoverallCpuUsage[] = new int[vmNum];
		int vmBalloonedMemory [] = new int[vmNum];
		int vmConsumedOverheadMemory [] = new int[vmNum];
		int vmFtLogBandwidth[] = new int[vmNum];
		int vmFtSecondaryLatency [] = new int[vmNum];
		int vmGuestMemoryUsage [] = new int[vmNum];
		int vmOverallCpuDemand  [] = new int[vmNum];
		int vmPrivateMemory [] = new int[vmNum];
		int vmSharedMemory [] = new int[vmNum];
		int vmStaticCpuEntiltlement [] = new int[vmNum];
		int vmStaticmemoryEntiltement [] = new int[vmNum];
		int vmSwappedMemory [] = new int[vmNum];
		
		DBCollection vmCollection = db.getCollection("VM");
		BasicDBObject vmfindQuery = new BasicDBObject();
		BasicDBObject vmfields = new BasicDBObject();
		fields.put("vm", 1);
		fields.put("VirtualMachineSummary", 1);
		vmfindQuery.put("timestamp", BasicDBObjectBuilder.start("$lte",endTime.toString()).add("$gt",startTime.toString()).get());
		DBCursor vmcursor = vmCollection.find(vmfindQuery,vmfields);
		//int vmCount = vmcursor.count();
		
		int VMvmMemoryCount[] = new int[vmNum];
		while(vmcursor.hasNext()) 
		{
			DBObject result = (DBObject)vmcursor.next();
			//System.out.println(result.toString());
			vm = (String)result.get("vm");
			int vmID=0;
			 for(;vmID<vmNum;vmID++)
			 {
				 if(vm.equals(vmList[vmID]))
					 break;
			 }
			
			BasicDBObject VMSummary =(BasicDBObject) result.get("VirtualMachineSummary");
			
			if(VMSummary!=null)
			{
				VMvmMemoryCount[vmID]++;
				 vmoverallCpuUsage[vmID] += Integer.parseInt((String)VMSummary.get("OverallCpuUsage"));
				 vmBalloonedMemory[vmID] += Integer.parseInt((String)VMSummary.get("BalloonedMemory"));
				 vmConsumedOverheadMemory[vmID] += Integer.parseInt((String)VMSummary.get("ConsumedOverheadMemory"));
				 vmFtLogBandwidth[vmID] += Integer.parseInt((String)VMSummary.get("FtLogBandwidth"));
				 vmFtSecondaryLatency[vmID] += Integer.parseInt((String)VMSummary.get("FtSecondaryLatency"));
			     vmGuestMemoryUsage[vmID] += Integer.parseInt((String)VMSummary.get("GuestMemoryUsage"));
			     vmOverallCpuDemand[vmID] += Integer.parseInt((String)VMSummary.get("OverallCpuDemand"));
			     vmPrivateMemory[vmID] += Integer.parseInt((String)VMSummary.get("PrivateMemory"));
			     vmSharedMemory[vmID] += Integer.parseInt((String)VMSummary.get("SharedMemory"));
			     vmStaticCpuEntiltlement[vmID] += Integer.parseInt((String)VMSummary.get("StaticCpuEntitlement"));
			     vmStaticmemoryEntiltement[vmID] += Integer.parseInt((String)VMSummary.get("StaticMemoryEntitlement"));
			     vmSwappedMemory[vmID] += Integer.parseInt((String)VMSummary.get("SwappedMemory"));
			}
		
		
			if(VMvmMemoryCount[vmID]>0)
			{
				 vmoverallCpuUsage[vmID]/=VMvmMemoryCount[vmID];
				 vmBalloonedMemory[vmID]/=VMvmMemoryCount[vmID];
				 vmConsumedOverheadMemory[vmID]/=VMvmMemoryCount[vmID];
				 vmFtLogBandwidth[vmID]/=VMvmMemoryCount[vmID];
				 vmFtSecondaryLatency[vmID]/=VMvmMemoryCount[vmID];
				 vmGuestMemoryUsage[vmID]/=VMvmMemoryCount[vmID];
				 vmOverallCpuDemand[vmID]/=VMvmMemoryCount[vmID];
				 vmPrivateMemory[vmID]/=VMvmMemoryCount[vmID];
				 vmSharedMemory[vmID]/=VMvmMemoryCount[vmID];
				 vmStaticCpuEntiltlement[vmID]/=VMvmMemoryCount[vmID];
				 vmStaticmemoryEntiltement[vmID]/=VMvmMemoryCount[vmID];
				 vmSwappedMemory[vmID]/=VMvmMemoryCount[vmID];
			}
		}
		
		for(int i=0;i<vmNum;i++)
		{
			try {
					Statement insertStmt = con.createStatement();
					String query = "Insert into VM_Stats(VMName,TimeStamp,OverallCpuUsage,BalloonedMemory,ConsumedOverheadMemory,FtLogBandwidth," +
							"FtSecondaryLatency,GuestMemoryUsage,OverallCpuDemand,PrivateMemory,SharedMemory,StaticCpuEntiltlement,StaticmemoryEntiltement,SwappedMemory) Values ('"+vmList[i]+"','"+endTime+"',"+vmoverallCpuUsage[i]+","+vmBalloonedMemory[i]+
							","+vmConsumedOverheadMemory[i]+","+vmFtLogBandwidth[i]+","+vmFtSecondaryLatency[i]+","+vmGuestMemoryUsage[i]+","+vmOverallCpuDemand[i]+","+vmPrivateMemory[i]+
							","+vmSharedMemory[i]+","+vmStaticCpuEntiltlement[i]+","+vmStaticmemoryEntiltement[i]+","+vmSwappedMemory[i]+")";
					//System.out.println(query);
					int updateResult = insertStmt.executeUpdate(query);
					//System.out.println("Number of Rows updated: "+updateResult);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void hourlyRollup(Date date)
	{
		//Reduce Host data
		String hostList[] = {"130.65.133.65","130.65.133.66","130.65.133.67","130.65.133.68","130.65.133.69"};
		int hostNum = hostList.length;
		String query = "Select * from Host_Stats where TimeStamp Between '"+date.toString()+"' and '"+new Date(date.getTime()+60*60000).toString()+"'";
		//System.out.println(query);
		try {
			PreparedStatement hourlyRollUpQuery = con.prepareStatement(query);
			ResultSet result = hourlyRollUpQuery.executeQuery();
			//System.out.println(result.getFetchSize());
			int overallCpuUsage[] = new int[hostNum]; 
			int BalloonedMemory[] = new int[hostNum];
			int ConsumedOverheadMemory[] = new int[hostNum];
			int FtLogBandwidth[] = new int[hostNum];
			int FtSecondaryLatency[] = new int[hostNum];
			int GuestMemoryUsage[] = new int[hostNum];
			int OverallCpuDemand[] = new int[hostNum];
			int PrivateMemory[] = new int[hostNum];
			int SharedMemory[] = new int[hostNum];
			int StaticCpuEntitlement[] = new int[hostNum];
			int StaticMemoryEntitlement[] = new int[hostNum];
			int SwappedMemory[] = new int[hostNum];
			int MaxRunningVMs[] = new int[hostNum];
			
			while(result.next())
			{
				String host = result.getString("HostName");
				int hostID=0;
				 for(;hostID<hostNum;hostID++)
				 {
					 if(host.equals(hostList[hostID]))
						 break;
				 }
				 overallCpuUsage[hostID]+= result.getInt("overallCpuUsage");
				 BalloonedMemory[hostID]+= result.getInt("BalloonedMemory");
				 ConsumedOverheadMemory[hostID]+= result.getInt("ConsumedOverheadMemory");
				 FtLogBandwidth[hostID]+= result.getInt("FtLogBandwidth");
				 FtSecondaryLatency[hostID]+= result.getInt("FtSecondaryLatency");
				 GuestMemoryUsage[hostID]+= result.getInt("GuestMemoryUsage");
				 OverallCpuDemand[hostID]+= result.getInt("OverallCpuDemand");
				 PrivateMemory[hostID]+= result.getInt("PrivateMemory");
				 SharedMemory[hostID]+= result.getInt("SharedMemory");
				 StaticCpuEntitlement[hostID]+= result.getInt("StaticCpuEntiltlement");
				 StaticMemoryEntitlement[hostID]+= result.getInt("StaticmemoryEntiltement");
				 SwappedMemory[hostID]+= result.getInt("SwappedMemory");
				 MaxRunningVMs[hostID]+= result.getInt("MaxRunningVMs");
				
			}
			hourlyRollUpQuery.close();

			for(int i=0;i<hostNum;i++)
			{
				overallCpuUsage[i]/= 12;
				BalloonedMemory[i]/=12;
				ConsumedOverheadMemory[i]/=12;
				FtLogBandwidth[i]/=12;
				FtSecondaryLatency[i]/=12;
				GuestMemoryUsage[i]/=12;
				OverallCpuDemand[i]/=12;
				PrivateMemory[i]/=12;
				SharedMemory[i]/=12;
				StaticCpuEntitlement[i]/=12;
				StaticMemoryEntitlement[i]/=12;
				SwappedMemory[i]/=12;
				MaxRunningVMs[i]/=12;
				
				Statement insertStmt = con.createStatement();
				String insertquery = "Insert into HourlyHost_Stats(HostName,TimeStamp,OverallCpuUsage,BalloonedMemory,ConsumedOverheadMemory,FtLogBandwidth," +
						"FtSecondaryLatency,GuestMemoryUsage,OverallCpuDemand,PrivateMemory,SharedMemory,StaticCpuEntiltlement,StaticmemoryEntiltement,SwappedMemory,MaxRunningVMs) Values ('"+hostList[i]+"','"+date+"',"+overallCpuUsage[i]+","+BalloonedMemory[i]+
						","+ConsumedOverheadMemory[i]+","+FtLogBandwidth[i]+","+FtSecondaryLatency[i]+","+GuestMemoryUsage[i]+","+OverallCpuDemand[i]+","+PrivateMemory[i]+
						","+SharedMemory[i]+","+StaticCpuEntitlement[i]+","+StaticMemoryEntitlement[i]+","+SwappedMemory[i]+","+MaxRunningVMs[i]+")";
				//System.out.println(query);
				int updateResult = insertStmt.executeUpdate(insertquery);
				//System.out.println("Number of Rows updated: "+updateResult);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//VM Rollup
		String vmList[] = {"Team18_VM_Ubuntu","Team18_VM_Ubuntu1","Team18_VM_Ubuntu2","Team18_VM_Ubuntu3","Team18_VM_Ubuntu4","Team18_VM_Ubuntu5","Team18_VM_Ubuntu6","Team18_VM_Ubuntu7"};
		int vmNum = vmList.length;
		int vmoverallCpuUsage[] = new int[vmNum];
		int vmBalloonedMemory [] = new int[vmNum];
		int vmConsumedOverheadMemory [] = new int[vmNum];
		int vmFtLogBandwidth[] = new int[vmNum];
		int vmFtSecondaryLatency [] = new int[vmNum];
		int vmGuestMemoryUsage [] = new int[vmNum];
		int vmOverallCpuDemand  [] = new int[vmNum];
		int vmPrivateMemory [] = new int[vmNum];
		int vmSharedMemory [] = new int[vmNum];
		int vmStaticCpuEntiltlement [] = new int[vmNum];
		int vmStaticmemoryEntiltement [] = new int[vmNum];
		int vmSwappedMemory [] = new int[vmNum];
		
		String vmquery = "Select * from VM_Stats where TimeStamp Between '"+date.toString()+"' and '"+new Date(date.getTime()+60*60000).toString()+"'";
		try {
			PreparedStatement vmhourlyRollUpQuery = con.prepareStatement(vmquery);
			ResultSet vmresult = vmhourlyRollUpQuery.executeQuery();
			while(vmresult.next())
			{
				String vm = vmresult.getString("VMName");
				int vmID=0;
				 for(;vmID<hostNum;vmID++)
				 {
					 if(vm.equals(hostList[vmID]))
						 break;
				 }
				 
				 vmoverallCpuUsage[vmID] += vmresult.getInt("OverallCpuUsage");
				 vmBalloonedMemory[vmID] += vmresult.getInt("BalloonedMemory");
				 vmConsumedOverheadMemory[vmID] +=vmresult.getInt("ConsumedOverheadMemory");
				 vmFtLogBandwidth[vmID] += vmresult.getInt("FtLogBandwidth");
				 vmFtSecondaryLatency[vmID] += vmresult.getInt("FtSecondaryLatency");
			     vmGuestMemoryUsage[vmID] += vmresult.getInt("GuestMemoryUsage");
			     vmOverallCpuDemand[vmID] += vmresult.getInt("OverallCpuDemand");
			     vmPrivateMemory[vmID] += vmresult.getInt("PrivateMemory");
			     vmSharedMemory[vmID] += vmresult.getInt("SharedMemory");
			     vmStaticCpuEntiltlement[vmID] += vmresult.getInt("StaticCpuEntiltlement");
			     vmStaticmemoryEntiltement[vmID] += vmresult.getInt("StaticmemoryEntiltement");
			     vmSwappedMemory[vmID] += vmresult.getInt("SwappedMemory");
			}
			
			for(int i=0;i<vmNum;i++)
			{
				vmoverallCpuUsage[i]/= 12;
				vmBalloonedMemory[i]/=12;
				vmConsumedOverheadMemory[i]/=12;
				vmFtLogBandwidth[i]/=12;
				vmFtSecondaryLatency[i]/=12;
				vmGuestMemoryUsage[i]/=12;
				vmOverallCpuDemand[i]/=12;
				vmPrivateMemory[i]/=12;
				vmSharedMemory[i]/=12;
				vmStaticCpuEntiltlement[i]/=12;
				vmStaticmemoryEntiltement[i]/=12;
				vmSwappedMemory[i]/=12;
				
				Statement insertStmt = con.createStatement();
				String insertquery = "Insert into HourlyVM_Stats(VMName,TimeStamp,OverallCpuUsage,BalloonedMemory,ConsumedOverheadMemory,FtLogBandwidth," +
						"FtSecondaryLatency,GuestMemoryUsage,OverallCpuDemand,PrivateMemory,SharedMemory,StaticCpuEntiltlement,StaticmemoryEntiltement,SwappedMemory) Values ('"+vmList[i]+"','"+date+"',"+vmoverallCpuUsage[i]+","+vmBalloonedMemory[i]+
						","+vmConsumedOverheadMemory[i]+","+vmFtLogBandwidth[i]+","+vmFtSecondaryLatency[i]+","+vmGuestMemoryUsage[i]+","+vmOverallCpuDemand[i]+","+vmPrivateMemory[i]+
						","+vmSharedMemory[i]+","+vmStaticCpuEntiltlement[i]+","+vmStaticmemoryEntiltement[i]+","+vmSwappedMemory[i]+")";
				//System.out.println(query);
				int updateResult = insertStmt.executeUpdate(insertquery);
				//System.out.println("Number of Rows updated: "+updateResult);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dailyRollup()
	{
		
	}
	
	public void fiveMinDataPurge(Date date)
	{
		Date currentTime = date;
		Date purgeTime = new Date(date.getTime()-60*60000);
		String delQuery = "Delete from Host_Stats where TimeStamp < '"+purgeTime+"'";
		System.out.println(delQuery);
		try {
			Statement stmt = con.createStatement();
			int result = stmt.executeUpdate(delQuery);
			System.out.println(result);
			
			delQuery = "Delete from VM_Stats where TimeStamp < '"+purgeTime+"'";
			result = stmt.executeUpdate(delQuery);
			System.out.println(result);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void hourlyDataPurge(Date date)
	{
		Date currentTime = date;
		Date purgeTime = new Date(date.getTime()-24*60*60000);
		System.out.println(purgeTime.getTime());
		String delQuery = "Delete from HourlyHost_Stats where TimeStamp < '"+purgeTime+"'";
		//String delQuery = "Delete from HourlyHost_Stats where TimeStamp < strtotime('-1 day')";
		//String delQuery = "Delete from HourlyHost_Stats where TimeStamp < UNIX_TIMESTAMP(DATE_SUB('"+date+"', INTERVAL 1 DAY))";
		System.out.println(delQuery);
		try {
			Statement stmt = con.createStatement();
			int result = stmt.executeUpdate(delQuery);
			System.out.println(result);
			
			delQuery = "Delete from HourlyVM_Stats where TimeStamp < '"+purgeTime+"'";
			//delQuery = "Delete from HourlyVM_Stats where TimeStamp < DATE_SUB('"+date+"', INTERVAL 1 DAY)";
			result = stmt.executeUpdate(delQuery);
			System.out.println(result);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void RunAnalyzer()
	{
		 //AnalyzerEngine test = null;
		try {
			final AnalyzerEngine test = new AnalyzerEngine();
		
		Date inputDate;
		try {
			//inputDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2013-11-24 16:00:00");
			inputDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2013-11-22 16:00:00");
			//inputDate = new Date("2013-11-22 16:00:00");
		//System.out.println(inputDate);
		
		Date lastUpdateTime = new Date(inputDate.getTime()+60*60000);
		//test.hourlyRollup(inputDate);

		/*while(inputDate.getTime()<lastUpdateTime.getTime())
		{
		//test.fiveMinReducer("2013-11-25 16:23:21");
		test.fiveMinReducer(inputDate);
		inputDate = new Date(inputDate.getTime()+5*60000);
		}*/
		//test.fiveMinDataPurge(inputDate);
		//test.hourlyDataPurge(inputDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		new Thread()
		{
			public void run()
			{
				while(true)
				{
				test.fiveMinReducer(new Date());
					try {
						Thread.sleep(5*60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		new Thread()
		{
			public void run()
			{
				while(true)
				{
				test.hourlyRollup(new Date());

					try {
						Thread.sleep(60*60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			};
		}.start();
		}
		catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
}



