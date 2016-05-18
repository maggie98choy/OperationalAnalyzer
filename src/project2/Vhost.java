package project2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Vhost {
	public String HostName;
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
	public int MaxRunningVMs;
	public Vhost(ResultSet vhost) {
		try {
			this.HostName = vhost.getString("HostName");
			String time = vhost.getString("timestamp");
			DateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
			this.timestamp = sdf.parse(time);
			this.OverallCpuUsage = vhost.getInt("OverallCpuUsage");
			this.BalloonedMemory = vhost.getInt("BalloonedMemory");
			this.ConsumedOverheadMemory = vhost.getInt("ConsumedOverheadMemory");
			this.DistributedCpuEntiltlement = vhost.getInt("DistributedCpuEntiltlement");
			this.FtLogBandwidth = vhost.getInt("FtLogBandwidth");
			this.FtSecondaryLatency = vhost.getInt("FtSecondaryLatency");
			this.GuestMemoryUsage = vhost.getInt("GuestMemoryUsage");
			this.OverallCpuDemand = vhost.getInt("OverallCpuDemand");
			this.PrivateMemory = vhost.getInt("PrivateMemory");
			this.SharedMemory = vhost.getInt("SharedMemory");
			this.StaticCpuEntiltlement = vhost.getInt("StaticCpuEntiltlement");
			this.StaticmemoryEntiltement = vhost.getInt("StaticmemoryEntiltement");
			this.SwappedMemory = vhost.getInt("SwappedMemory");
			this.MaxRunningVMs = vhost.getInt("MaxRunningVMs");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
