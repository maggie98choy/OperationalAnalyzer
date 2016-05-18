package project2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VM {
	public String VMName;
	public Date timestamp;
	public int OverallCpuUsage;
	public int BalloonedMemory;
	public int ConsumedOverheadMemory;
	public int DistributedCpuEntiltlement;
	public int FtLogBandwidth;
	public int FtSecondaryLatency;
	public int GuestMemoryUsage;
	public int OverallCpuDemand;
	public int PrivateMemory;
	public int SharedMemory;
	public int StaticCpuEntiltlement;
	public int StaticmemoryEntiltement;
	public int SwappedMemory;
	
	public VM(ResultSet vm) {
		try {
			this.VMName = vm.getString("VMName");
			String time = vm.getString("timestamp");
			DateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
			this.timestamp = sdf.parse(time);
			this.OverallCpuUsage = vm.getInt("OverallCpuUsage");
			this.BalloonedMemory = vm.getInt("BalloonedMemory");
			this.ConsumedOverheadMemory = vm.getInt("ConsumedOverheadMemory");
			this.DistributedCpuEntiltlement = vm.getInt("DistributedCpuEntiltlement");
			this.FtLogBandwidth = vm.getInt("FtLogBandwidth");
			this.FtSecondaryLatency = vm.getInt("FtSecondaryLatency");
			this.GuestMemoryUsage = vm.getInt("GuestMemoryUsage");
			this.OverallCpuDemand = vm.getInt("OverallCpuDemand");
			this.PrivateMemory = vm.getInt("PrivateMemory");
			this.SharedMemory = vm.getInt("SharedMemory");
			this.StaticCpuEntiltlement = vm.getInt("StaticCpuEntiltlement");
			this.StaticmemoryEntiltement = vm.getInt("StaticmemoryEntiltement");
			this.SwappedMemory = vm.getInt("SwappedMemory");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
