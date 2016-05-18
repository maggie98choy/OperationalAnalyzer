package project2;

import com.mongodb.MongoClient;
import com.mongodb.DB;
import java.net.UnknownHostException;


public class DBConnection
{
	
	private MongoClient mongoClient;
	private DB db;

	public DBConnection() throws UnknownHostException
	{
		mongoClient = new MongoClient("54.245.7.205",27017);
		db = mongoClient.getDB("283Project2");
		
		/*
		DBCollection table = db.getCollection("user");
		BasicDBObject document = new BasicDBObject();
		
		document.put("name", "Maggie Choy");
		document.put("createdDate", new Date());
		table.insert(document);
		*/
				
	}
	
	public DB getDB()
	{
		return db;
	}
	
	
}
