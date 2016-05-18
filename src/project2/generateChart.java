package project2;

import java.io.*;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.googlecode.charts4j.*;
import com.mongodb.*;
import java.util.*;
import java.util.Date;
 
public class generateChart 
{
	private static Boolean firstRun = true;
    public static void main(String[] args) {
    	//String url = "jdbc:mysql://localhost:3306/";
    	String url = "jdbc:mysql://54.245.7.205:3306/";
    	String dbName = "283project2";
		String driver = "com.mysql.jdbc.Driver";
		//String userName = "root";//cmpe283 
		String userName = "cmpe283";
		String password = "12!@qwQW";
		Boolean bcontinue = true;
		do {
			try {
				Class.forName(driver).newInstance();
				Connection conn = DriverManager.getConnection(url+dbName,userName,password);
				System.out.println("Connected to " + conn.getCatalog());
				Statement st = conn.createStatement();
				
				List<String> urls = new ArrayList<String>();
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.HOUR, -1);
				Date oneHourBack = cal.getTime();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -24);
				Date dayBack = calendar.getTime();
				
				ResultSet hosts = st.executeQuery("SELECT * from host_stats;");
				System.out.println("-----------------------------------------");
				System.out.println("HOSTs: ");
				List<Vhost> vHosts = new ArrayList<Vhost>();
				List<String> VhostNames = new ArrayList<String>();
				while (hosts.next()) {
					Vhost vhost = new Vhost(hosts);
	//				String name = hosts.getString("HostName");
	//				String time = hosts.getString("timestamp");
	//				DateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
	//				Date date = sdf.parse(time);
					if (vhost.timestamp.after(oneHourBack)) {
						vHosts.add(vhost);
					}
					if (!VhostNames.contains(vhost.HostName)) {
						VhostNames.add(vhost.HostName);
					}
					DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss zzz");
					System.out.println(vhost.HostName + "\t" + sdf2.format(vhost.timestamp));
				}
				System.out.println(VhostNames.size());
				System.out.println(vHosts.size());
				
				
				ResultSet hostsHourly = st.executeQuery("SELECT * from HourlyHost_Stats;");
				System.out.println("-----------------------------------------");
				System.out.println("HOSTs hourly: ");
				List<Vhost> vHostsHourly = new ArrayList<Vhost>();
				List<String> VhostNamesHourly = new ArrayList<String>();
				while (hostsHourly.next()) {
					Vhost vhost = new Vhost(hostsHourly);
	//				String name = hosts.getString("HostName");
	//				String time = hosts.getString("timestamp");
	//				DateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
	//				Date date = sdf.parse(time);
					if (vhost.timestamp.after(dayBack)) {
						vHostsHourly.add(vhost);
					}
					if (!VhostNamesHourly.contains(vhost.HostName)) {
						VhostNamesHourly.add(vhost.HostName);
					}
					DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss zzz");
					System.out.println(vhost.HostName + "\t" + sdf2.format(vhost.timestamp));
				}
				System.out.println(VhostNamesHourly.size());
				
				
				ResultSet vms = st.executeQuery("SELECT * from vm_stats;");
				System.out.println("-----------------------------------------");
				System.out.println("VMs:");
				List<VM> VMs = new ArrayList<VM>();
				List<String> VMNames = new ArrayList<String>();
				while (vms.next()) {
					VM vm = new VM(vms);
					if (vm.timestamp.after(oneHourBack)) {
						VMs.add(vm);
					}
					if (!VMNames.contains(vm.VMName)) {
						VMNames.add(vm.VMName);
					}
	//				String name = vms.getString("VMName");
	//				String time = vms.getString("timestamp");
					DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss zzz");
					System.out.println(vm.VMName + "\t" + sdf2.format(vm.timestamp));
				}
				System.out.println(VMNames.size());
				
				
				ResultSet vmsHourly = st.executeQuery("SELECT * from HourlyVM_Stats;");
				System.out.println("-----------------------------------------");
				System.out.println("VMs Hourly:");
				List<VM> VMsHourly = new ArrayList<VM>();
				List<String> VMNamesHourly = new ArrayList<String>();
				while (vmsHourly.next()) {
					VM vm = new VM(vmsHourly);
					if (vm.timestamp.after(dayBack)) {
						VMsHourly.add(vm);
					}
					if (!VMNamesHourly.contains(vm.VMName)) {
						VMNamesHourly.add(vm.VMName);
					}
	//				String name = vms.getString("VMName");
	//				String time = vms.getString("timestamp");
					DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss zzz");
					System.out.println(vm.VMName + "\t" + sdf2.format(vm.timestamp));
				}
				System.out.println(VMNamesHourly.size());
				
				urls.addAll(displayLastVM(VMs, VMNames));
				urls.addAll(displayLastVhost(vHosts, VhostNames));
				urls.addAll(displayVM(VMs, VMNames, false));
				urls.addAll(displayVhost(vHosts, VhostNames, false));
				urls.addAll(displayVM(VMsHourly, VMNamesHourly, true));
				urls.addAll(displayVhost(vHostsHourly, VhostNamesHourly, true));
				
				generateHTML(urls, VMNames.size(), VhostNames.size());
			    conn.close();
			    //Wait 5min
			    Thread.sleep(300000);
			} catch (Exception e) {
				  e.printStackTrace();
			}
			
		} while(bcontinue);
//		//mongoDB
//		List<String> tables = new ArrayList<String>();
//		tables.add("ESXiLog");
//		tables.add("Host");
//		tables.add("VM");
//		MongoClient mongoClient;
//		try {
//			mongoClient = new MongoClient("54.245.7.205",27017);
//			DB db = mongoClient.getDB("283Project2");
//			Set<String> colls = db.getCollectionNames();
//			
//			for (String s : colls) {
//			    if (tables.contains(s)) {
//			    	System.out.println(s);
//			    	DBCollection coll = db.getCollection(s);
//			    }
//			}
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		
    }
    public static void goToURL(String url) {
	  	String os = System.getProperty("os.name").toLowerCase();
	    Runtime rt = Runtime.getRuntime();
	    try{
	    	if (os.indexOf( "win" ) >= 0) {  
				// this doesn't support showing urls in the form of "page.html#nameLink" 
				rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
	    	} else if (os.indexOf( "mac" ) >= 0) {
				rt.exec( "open " + url);
			} else {
				return;
			}
	    }catch (Exception e){
			return;
		}
    }
    public static String simpleLineChart(String title, List<List<String>> labels, List<ArrayList<Integer>> percentages, int max, String yLabel)
    {
//    	final int NUM_POINTS = 25;
//        final double[] competition = new double[NUM_POINTS];
//        final double[] mywebsite = new double[NUM_POINTS];
//        for (int i = 0; i < NUM_POINTS; i++) {
//            competition[i] = 100-(Math.cos(30*i*Math.PI/180)*10 + 50)*i/20;
//            mywebsite[i] = (Math.cos(30*i*Math.PI/180)*10 + 50)*i/20;
//        }
//        Line line1 = Plots.newLine(Data.newData(mywebsite), Color.newColor("CA3D05"), "My Website.com");
//        line1.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
//        line1.addShapeMarkers(Shape.DIAMOND, Color.newColor("CA3D05"), 12);
//        line1.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
//        Line line2 = Plots.newLine(Data.newData(competition), Color.SKYBLUE, "Competition.com");
//        line2.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
//        line2.addShapeMarkers(Shape.DIAMOND, Color.SKYBLUE, 12);
//        line2.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
        
        int i = 0;
        List<Line> lines = new ArrayList<Line>();
        System.out.println(title + max);
        for (String label : labels.get(0)) {
        	Line line = Plots.newLine(DataUtil.scaleWithinRange(0, max, percentages.get(i)), Color.newColor(randomColor()), label);
        	lines.add(line);
        	i++;
        }
        // Defining chart.
//        LineChart chart = GCharts.newLineChart(line1, line2);
        LineChart chart = GCharts.newLineChart(lines);
        chart.setSize(666, 450);
        chart.setTitle(title, Color.WHITE, 14);
        //chart.addHorizontalRangeMarker(40, 60, Color.newColor(Color.RED, 30));
        //chart.addVerticalRangeMarker(70, 90, Color.newColor(Color.GREEN, 30));
        //chart.setGrid(25, 25, 3, 2);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels(labels.get(1));
        xAxis.setAxisStyle(axisStyle);
        //AxisLabels xAxis2 = AxisLabelsFactory.newAxisLabels("2007", "2007", "2008", "2008", "2008");
        //xAxis2.setAxisStyle(axisStyle);
        AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Time", 50.0);
        xAxis3.setAxisStyle(AxisStyle.newAxisStyle(Color.WHITE, 14, AxisTextAlignment.CENTER));
        AxisLabels yAxis = AxisLabelsFactory.newNumericRangeAxisLabels(0,max);
        yAxis.setAxisStyle(axisStyle);
        AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels(yLabel, 50.0);
        yAxis2.setAxisStyle(AxisStyle.newAxisStyle(Color.WHITE, 14, AxisTextAlignment.CENTER));
        yAxis2.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(xAxis);
        //chart.addXAxisLabels(xAxis2);
        chart.addXAxisLabels(xAxis3);
        chart.addYAxisLabels(yAxis);
        chart.addYAxisLabels(yAxis2);

        // Defining background and chart fills.
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);
        String urlChart = chart.toURLString();
        System.out.println(urlChart);
        //goToURL(urlChart);
        return urlChart;
    }
    public static void simpleBarChart(String title, List<List<String>> labels, List<ArrayList<Integer>> percentages)
    {
    	int i = 0;
    	List<BarChartPlot> plots = new ArrayList<BarChartPlot>();
    	for (String label : labels.get(0)) {
    		BarChartPlot plot = Plots.newBarChartPlot(Data.newData(percentages.get(i)), Color.newColor(randomColor()), label);
    		plots.add(plot);
    		i++;
    	}
//    	BarChartPlot team1 = Plots.newBarChartPlot(Data.newData(25, 43, 12, 30), Color.BLUEVIOLET, "Team A");
//        BarChartPlot team2 = Plots.newBarChartPlot(Data.newData(8, 35, 11, 5), Color.ORANGERED, "Team B");
//        BarChartPlot team3 = Plots.newBarChartPlot(Data.newData(10, 20, 30, 30), Color.LIMEGREEN, "Team C");
//
//        // Instantiating chart.
//        BarChart chart = GCharts.newBarChart(team1, team2, team3);
    	BarChart chart = GCharts.newBarChart(plots);
        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);
        AxisLabels y = AxisLabelsFactory.newAxisLabels(labels.get(2).get(1), 50.0);
        y.setAxisStyle(axisStyle);
        AxisLabels x = AxisLabelsFactory.newAxisLabels(labels.get(2).get(0), 50.0);
        x.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels(labels.get(1)));
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, 100));
        chart.addYAxisLabels(y);
        chart.addXAxisLabels(x);

        chart.setSize(600, 450);
        chart.setBarWidth(100);
        chart.setSpaceWithinGroupsOfBars(20);
        chart.setDataStacked(true);
        chart.setTitle(title, Color.BLACK, 16);
        chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.ALICEBLUE));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        String urlChart = chart.toURLString();
        System.out.println(urlChart);
        //goToURL(urlChart);
    }
    public static void multiBarChart(String title, List<List<String>> labels, List<List<Integer>> percentages, List<Integer> numericLabels)
    {
    	//final int MAX_MEDALS = 51;
    	int i = 0;
    	List<BarChartPlot> plots = new ArrayList<BarChartPlot>();
    	for (String label : labels.get(0)) {
    		Data data = DataUtil.scaleWithinRange(0, 100.0, percentages.get(i));
    		BarChartPlot plot = Plots.newBarChartPlot(data, Color.newColor(randomColor()), label);
    		plots.add(plot);
    		i++;
    	}
    	BarChart chart = GCharts.newBarChart(plots);
        /*Data goldData= DataUtil.scaleWithinRange(0, 100.0, Arrays.asList(MAX_MEDALS, 36, 23, 19, 16));
        Data silverData= DataUtil.scaleWithinRange(0, 100.0, Arrays.asList(21, 38, 21, 13, 10));
        Data bronzeData= DataUtil.scaleWithinRange(0, 100.0, Arrays.asList(28, 36, 28, 15, 15));
        BarChartPlot gold = Plots.newBarChartPlot(goldData, Color.GOLD, "Gold");
        BarChartPlot silver = Plots.newBarChartPlot(silverData, Color.SILVER, "Silver");
        BarChartPlot bronze = Plots.newBarChartPlot(bronzeData, Color.BROWN, "Bronze");
        BarChart chart = GCharts.newBarChart(gold, silver,  bronze);*/
        
        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);
        //AxisLabels country = AxisLabelsFactory.newAxisLabels("Country", 50.0);
        //country.setAxisStyle(axisStyle);
        AxisLabels x = AxisLabelsFactory.newAxisLabels(labels.get(1));
        x.setAxisStyle(axisStyle);
        AxisLabels y = AxisLabelsFactory.newAxisLabels("Percentage", 100.0);
        y.setAxisStyle(axisStyle);
        AxisLabels yCount = AxisLabelsFactory.newNumericRangeAxisLabels(0, 100.0);
        yCount.setAxisStyle(axisStyle);


        // Adding axis info to chart.
        chart.addXAxisLabels(yCount);
        chart.addXAxisLabels(y);
        chart.addYAxisLabels(x);
        //chart.addYAxisLabels(country);
        chart.addTopAxisLabels(yCount);
        chart.setHorizontal(true);
        chart.setSize(450, 650);
        chart.setSpaceBetweenGroupsOfBars(30);

        chart.setTitle(title, Color.BLACK, 16);
        ///51 is the max number of medals.
        chart.setGrid((50.0/100.0)*20, 600, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.LIGHTGREY));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("E37600"), 100);
        fill.addColorAndOffset(Color.newColor("DC4800"), 0);
        chart.setAreaFill(fill);
        String urlChart = chart.toURLString();
        System.out.println(urlChart);
        //goToURL(urlChart);
    }
    public static String radarChart(String title, List<String> labels, List<Integer> percentages, List<Integer> numericLabels)
    {
    	RadarPlot plot = Plots.newRadarPlot(Data.newData(percentages));
        Color plotColor = Color.newColor("CC3366");
        plot.addShapeMarkers(Shape.SQUARE, plotColor, 12);
        plot.addShapeMarkers(Shape.SQUARE, Color.WHITE, 8);
        plot.setColor(plotColor);
        plot.setLineStyle(LineStyle.newLineStyle(4, 1, 0));
        RadarChart chart = GCharts.newRadarChart(plot);
        chart.setTitle(title, Color.BLACK, 20);
        chart.setSize(500, 400);
        RadialAxisLabels radialAxisLabels = AxisLabelsFactory.newRadialAxisLabels(labels);
        radialAxisLabels.setRadialAxisStyle(Color.BLACK, 12);
        chart.addRadialAxisLabels(radialAxisLabels);
        AxisLabels contrentricAxisLabels = AxisLabelsFactory.newNumericAxisLabels(numericLabels);
        contrentricAxisLabels.setAxisStyle(AxisStyle.newAxisStyle(Color.BLACK, 12, AxisTextAlignment.RIGHT));
        chart.addConcentricAxisLabels(contrentricAxisLabels);
        String urlChart = chart.toURLString();
        System.out.println(urlChart);
        return urlChart;
        //goToURL(urlChart);
    }
    public static void pieChart(String title, List<String> labels, List<Integer> percentages)
    {
    	/*Slice s1 = Slice.newSlice(30, Color.newColor("CACACA"), "Safari", "Apple");
        Slice s2 = Slice.newSlice(30, Color.newColor("DF7417"), "Firefox", "Mozilla");
        Slice s3 = Slice.newSlice(30, Color.newColor("951800"), "Chrome", "Google");
        Slice s4 = Slice.newSlice(10, Color.newColor("01A1DB"), "Internet Explorer", "Microsoft");
        PieChart chart = GCharts.newPieChart(s1, s2, s3, s4);*/
        List<Slice> slices = new ArrayList<Slice>();
        for (int i = 0; i < labels.size(); i++) {
        	Slice slice = Slice.newSlice(percentages.get(i), Color.newColor(randomColor()), labels.get(i));
        	slices.add(slice);
        }
        PieChart chart = GCharts.newPieChart(slices);
        
        chart.setTitle(title, Color.BLACK, 16);
        chart.setSize(500, 200);
        chart.setThreeD(true);
        String urlChart = chart.toURLString();
        System.out.println(urlChart);
        //goToURL(urlChart);
    }
    public static String randomColor() {
    	Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
    	int min = 0;
    	int max = 16777214;
        int randomNum = rand.nextInt((max - min) + 1) + min;
    	String Hex=Integer.toHexString(randomNum);
    	for (int i = Hex.length(); i < 6; i++) {
    		Hex = "0" + Hex;
    	}
    	return Hex;
    }
    public static int maxCPU;
    public static int maxMemory;
    public static String yLabelCPU = "MHz";
    public static String yLabelMemory = "MB";
    
    public static List<String> displayVhost(List<Vhost> vHosts, List<String> VhostNames, Boolean Hourly) {
    	List<ArrayList<Integer>> ListCPUusage = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListCPUallocated = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListOverallDemand = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListMemoryUsage = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListMemoryAllocated = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListSharedMemory = new ArrayList<ArrayList<Integer>>();
    	for (String vHostName : VhostNames){
    		maxCPU = 1;
        	maxMemory = 1;
    		List<Integer> CPUusage = new ArrayList<Integer>();
        	List<Integer> CPUallocated = new ArrayList<Integer>();
        	List<Integer> CPUdemand = new ArrayList<Integer>();
        	List<Integer> MemoryUsage = new ArrayList<Integer>();
        	List<Integer> MemoryAllocated = new ArrayList<Integer>();
        	List<Integer> MemoryShared = new ArrayList<Integer>();
    		for (Vhost vHost : vHosts) {
    			if (vHost.HostName.equals(vHostName)) {
    				CPUusage.add(vHost.OverallCpuUsage);
    				CPUallocated.add(vHost.StaticCpuEntiltlement);
    				CPUdemand.add(vHost.OverallCpuDemand);
    				if (vHost.StaticCpuEntiltlement > maxCPU) {
    					maxCPU = vHost.StaticCpuEntiltlement + 100;
    				} 
    				if (vHost.OverallCpuDemand > maxCPU) {
    					maxCPU = vHost.OverallCpuDemand + 100;
    				}
    				if (vHost.OverallCpuUsage > maxCPU) {
    					maxCPU = vHost.OverallCpuUsage + 100;
    				}
    				MemoryUsage.add(vHost.GuestMemoryUsage);
    				MemoryAllocated.add(vHost.StaticmemoryEntiltement);
    				MemoryShared.add(vHost.SharedMemory);
    				if (vHost.GuestMemoryUsage > maxMemory) {
    					maxMemory = vHost.GuestMemoryUsage + 50;
    				}
    				if (vHost.StaticmemoryEntiltement > maxMemory) {
    					maxMemory = vHost.StaticmemoryEntiltement + 50;
    				}
    				if (vHost.SharedMemory > maxMemory) {
    					maxMemory = vHost.SharedMemory + 100;
    				}
    			}
    		}
    		ListCPUusage.add((ArrayList<Integer>) CPUusage);
    		ListCPUallocated.add((ArrayList<Integer>) CPUallocated);
    		ListOverallDemand.add((ArrayList<Integer>) CPUdemand);
    		ListMemoryUsage.add((ArrayList<Integer>) MemoryUsage);
    		ListMemoryAllocated.add((ArrayList<Integer>) MemoryAllocated);
    		ListSharedMemory.add((ArrayList<Integer>) MemoryShared);
    	}
    	List<String> label = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
    	String span = "24h.";
    	int length = Math.min(24, ListCPUallocated.get(0).size());
    	if (!Hourly) {
    		label = Arrays.asList("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
    		span = "1h.";
    		length = Math.min(12, ListCPUallocated.get(0).size());
    	}
    	
    	List<String> urls = new ArrayList<String>();
    	for (int i = 0; i < VhostNames.size(); i++) {
			urls.add(simpleLineChart(VhostNames.get(i) + " CPU usage over " + span, Arrays.asList( Arrays.asList("CPU Usage", "CPU Allocated", "CPU Demand"), label.subList(0, length)), Arrays.asList(ListCPUusage.get(i), ListCPUallocated.get(i), ListOverallDemand.get(i)), maxCPU ,yLabelCPU));
    		urls.add(simpleLineChart(VhostNames.get(i) + " Memory usage over " + span, Arrays.asList( Arrays.asList("Memory Usage", "Memory Allocated", "Memory Demand"), label.subList(0, length)), Arrays.asList(ListMemoryUsage.get(i), ListMemoryAllocated.get(i), ListSharedMemory.get(i)), maxMemory ,yLabelMemory));
    	}
    	return urls;
    }
    public static List<String> displayVM(List<VM> VMs, List<String> VMNames, Boolean Hourly) {
    	List<ArrayList<Integer>> ListCPUusage = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListCPUallocated = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListOverallDemand = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListMemoryUsage = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListMemoryAllocated = new ArrayList<ArrayList<Integer>>();
    	List<ArrayList<Integer>> ListSharedMemory = new ArrayList<ArrayList<Integer>>();
    	for (String vHostName : VMNames){
    		maxCPU = 1;
        	maxMemory = 1;
    		List<Integer> CPUusage = new ArrayList<Integer>();
        	List<Integer> CPUallocated = new ArrayList<Integer>();
        	List<Integer> CPUdemand = new ArrayList<Integer>();
        	List<Integer> MemoryUsage = new ArrayList<Integer>();
        	List<Integer> MemoryAllocated = new ArrayList<Integer>();
        	List<Integer> MemoryShared = new ArrayList<Integer>();
    		for (VM vm : VMs) {
    			if (vm.VMName.equals(vHostName)) {
    				CPUusage.add(vm.OverallCpuUsage);
    				CPUallocated.add(vm.StaticCpuEntiltlement);
    				CPUdemand.add(vm.OverallCpuDemand);
    				if (vm.StaticCpuEntiltlement > maxCPU) {
    					maxCPU = vm.StaticCpuEntiltlement + 100;
    				} 
    				if (vm.OverallCpuDemand > maxCPU) {
    					maxCPU = vm.OverallCpuDemand + 100;
    				}
    				if (vm.OverallCpuUsage > maxCPU) {
    					maxCPU = vm.OverallCpuUsage + 100;
    				}
    				MemoryUsage.add(vm.GuestMemoryUsage);
    				MemoryAllocated.add(vm.StaticmemoryEntiltement);
    				MemoryShared.add(vm.SharedMemory);
    				if (vm.GuestMemoryUsage > maxMemory) {
    					maxMemory = vm.GuestMemoryUsage + 50;
    				}
    				if (vm.StaticmemoryEntiltement > maxMemory) {
    					maxMemory = vm.StaticmemoryEntiltement + 50;
    				}
    				if (vm.SharedMemory > maxMemory) {
    					maxMemory = vm.SharedMemory + 100;
    				}
    			}
    		}
    		ListCPUusage.add((ArrayList<Integer>) CPUusage);
    		ListCPUallocated.add((ArrayList<Integer>) CPUallocated);
    		ListOverallDemand.add((ArrayList<Integer>) CPUdemand);
    		ListMemoryUsage.add((ArrayList<Integer>) MemoryUsage);
    		ListMemoryAllocated.add((ArrayList<Integer>) MemoryAllocated);
    		ListSharedMemory.add((ArrayList<Integer>) MemoryShared);
    	}
    	List<String> label = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
    	String span = "24h.";
    	int length = Math.min(24, ListCPUallocated.get(0).size());
    	if (!Hourly) {
    		label = Arrays.asList("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
    		span = "1h.";
    		length = Math.min(12, ListCPUallocated.get(0).size());
    	}
    	List<String> urls = new ArrayList<String>();
    	for (int i = 0; i < VMNames.size(); i++) {
    		urls.add(simpleLineChart(VMNames.get(i) + " CPU usage over " + span, Arrays.asList( Arrays.asList("CPU Usage", "CPU Allocated", "CPU Demand"), label.subList(0, length)), Arrays.asList(ListCPUusage.get(i), ListCPUallocated.get(i), ListOverallDemand.get(i)), maxCPU ,yLabelCPU));
    		urls.add(simpleLineChart(VMNames.get(i) + " Memory usage over " + span, Arrays.asList( Arrays.asList("Memory Usage", "Memory Allocated", "Memory Demand"), label.subList(0, length)), Arrays.asList(ListMemoryUsage.get(i), ListMemoryAllocated.get(i), ListSharedMemory.get(i)), maxMemory ,yLabelMemory));
    	}
    	return urls;
    }
    
    public static List<String> displayLastVM(List<VM> VMs, List<String> VMNames) {
    	List<Integer> ListCPU = new ArrayList<Integer>();
    	List<Integer> ListMemory = new ArrayList<Integer>();
    	for (String VMname : VMNames){
    		boolean first = true;
    		Date date = new Date();
    		int cpu = 0;
			int memory = 0;
    		for (VM vm : VMs) {
    			if (vm.VMName.equals(VMname) && first) {
    				first = false;
    				date = vm.timestamp;
    				cpu = Math.min((int) (vm.OverallCpuUsage * 1.0/(vm.StaticCpuEntiltlement+1)*100), (int) 100);
    				memory = Math.min((int) (vm.GuestMemoryUsage *1.0/(vm.StaticmemoryEntiltement+1)*100), (int) 100);
    			} else if (vm.VMName.equals(VMname) && vm.timestamp.compareTo(date) > 0) {
    				date = vm.timestamp;
    				cpu = Math.min((int) (vm.OverallCpuUsage*1.0/(vm.StaticCpuEntiltlement+1)*100), (int) 100);
    				memory = Math.min((int) (vm.GuestMemoryUsage*1.0/(vm.StaticmemoryEntiltement+1)*100), (int) 100);
    			}
    		}
    		ListCPU.add(cpu);
    		ListMemory.add(memory);
    	}
    	List<String> label = Arrays.asList("CPU", "Memory");
    	List<Integer> numericLabel = Arrays.asList(0, 20, 40, 60, 80, 100);
    	List<String> urls = new ArrayList<String>();
    	for (int i = 0; i < VMNames.size(); i++) {
    		urls.add(radarChart(VMNames.get(i) + " last performances.", label, Arrays.asList(ListCPU.get(i), ListMemory.get(i)), numericLabel));
    	}
    	return urls;
    }
    
    public static List<String> displayLastVhost(List<Vhost> vHosts, List<String> VhostNames) {
    	List<Integer> ListCPU = new ArrayList<Integer>();
    	List<Integer> ListMemory = new ArrayList<Integer>();
    	for (String vHostname : VhostNames){
    		boolean first = true;
    		Date date = new Date();
    		int cpu = 0;
			int memory = 0;
    		for (Vhost vHost : vHosts) {
    			if (vHost.HostName.equals(vHostname) && first) {
    				first = false;
    				date = vHost.timestamp;
    				cpu = Math.min((int) (vHost.OverallCpuUsage * 1.0/(vHost.StaticCpuEntiltlement+1)*100), (int) 100);
    				memory = Math.min((int) (vHost.GuestMemoryUsage *1.0/(vHost.StaticmemoryEntiltement+1)*100), (int) 100);
    			} else if (vHost.HostName.equals(vHostname) && vHost.timestamp.compareTo(date) > 0) {
    				date = vHost.timestamp;
    				cpu = Math.min((int) (vHost.OverallCpuUsage*1.0/(vHost.StaticCpuEntiltlement+1)*100), (int) 100);
    				memory = Math.min((int) (vHost.GuestMemoryUsage*1.0/(vHost.StaticmemoryEntiltement+1)*100), (int) 100);
    			}
    		}
    		ListCPU.add(cpu);
    		ListMemory.add(memory);
    	}
    	List<String> label = Arrays.asList("CPU", "Memory");
    	List<Integer> numericLabel = Arrays.asList(0, 20, 40, 60, 80, 100);
    	List<String> urls = new ArrayList<String>();
    	for (int i = 0; i < VhostNames.size(); i++) {
    		urls.add(radarChart(VhostNames.get(i) + " last performances.", label, Arrays.asList(ListCPU.get(i), ListMemory.get(i)), numericLabel));
    	}
    	return urls;
    }
    
    public static void generateHTML(List<String> urls, int numberVM, int numberHost){
    	try {
            File f = new File("generated.htm");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("<html>");
            bw.write("<meta http-equiv=\"refresh\" content=\"150\">");
            bw.write("<body>");
            bw.write("<h1>Generated graphs</h1>");
            boolean first = true;
            
            int i = 0;
            for (String url : urls) {
            	if (first) {
            		bw.write("<h3>Last 5 minutes:</h3>");
            		bw.write("<h4>VMs:</h4>");
            		first = false;
            	}	
            	if (i == numberVM) {
            		bw.write("<h4>Hosts:</h4>");
            	}
            	if (i == (numberVM + numberHost)) {
            		bw.write("<h3>Last hour:</h3>");
            		bw.write("<h4>VMs:</h4>");
            	}
            	if (i == (3*numberVM + numberHost)) {
            		bw.write("<h4>Hosts:</h4>");
            	}
            	if (i == 3*(numberVM + numberHost)) {
            		bw.write("<h3>Last 24hours:</h3>");
            		bw.write("<h4>VMs:</h4>");
            	}
            	if (i == (3*(numberVM + numberHost) + 2*numberVM)) {
            		bw.write("<h4>Hosts:</h4>");
            	}
            	bw.write("<img src=\"" + url +"\" alt=\"Error\">");
            	i++;
            }

            bw.write("</body>");
            bw.write("</html>");

            bw.close();
            if (firstRun) {
            	firstRun = false;
            	goToURL(f.toURI().toString());
            }
    	} catch (Exception e) {
			  e.printStackTrace();
		}
    }
}


