import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Date;


public class TestMongDB 
{
	
	
	public static void main(String[]args) throws UnknownHostException
	{
		MongoClient mongoClient = new MongoClient("54.245.7.205",27017);
		//mongoClient = new MongoClient("54.245.7.205",27017);
		
		DB db = mongoClient.getDB("283Project2");
		
		DBCollection table = db.getCollection("user");
		BasicDBObject document = new BasicDBObject();
		
		document.put("name", "KK");
		//document.put({'host':130.65.133.68, 'HostListSummary': {'CpuMhz' :2393, 'MemorySize' :3220688896, 'timestamp' :Fri Nov 22 18:17:38 PST 2013});
		document.put("createdDate", new Date());
		table.insert(document);
		System.out.println("Done!");
		
		
	}
}
