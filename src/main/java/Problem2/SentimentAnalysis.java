package Problem2;

import java.util.Iterator;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class SentimentAnalysis {
	public static void main(String args[]) {
		// MongoDB Configurations
		ConnectionString connectionString = new ConnectionString(
				"mongodb+srv://root:root@cluster0.jvs2l.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase cleaned_database = mongoClient.getDatabase("ProcessedDb");
		
		//All collections
		MongoIterable<String> collections = cleaned_database.listCollectionNames();
		for (String collection : collections) {
			MongoCollection current_collection = cleaned_database.getCollection(collection);
			FindIterable<Document> documents = current_collection.find();
			MongoCursor<Document> cursor = documents.iterator();
			while(cursor.hasNext())
			{
				Document document = cursor.next();
				System.out.println(document);
			}
		}

	}
}
