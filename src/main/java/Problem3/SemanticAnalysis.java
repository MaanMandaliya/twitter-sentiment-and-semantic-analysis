package Problem3;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Formatter;
import java.util.Iterator;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class SemanticAnalysis {
	public static void main(String args[]) throws JSONException {
		// MongoDB Configurations
		ConnectionString connectionString = new ConnectionString(
				"mongodb+srv://root:root@cluster0.jvs2l.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase cleaned_database = mongoClient.getDatabase("ReuterDb");
		MongoCollection news_collection = cleaned_database.getCollection("News");

		// to count total documents
		int n = (int) news_collection.countDocuments();
		System.out.println("Total Documents : " + n);

		// Formatting the table
		Formatter format = new Formatter();
		format.format("%86s\n", "-".repeat(86));
		format.format("|%-15s|%-30s|%-20s|%-15s|\n", "Search Query", "Documents Containing Term(df)",
				"Total Documents(N)", "Log10(N/df)");
		format.format("%86s\n", "-".repeat(86));

		// count term frequency-inverse document frequency (TF-IDF)
		FindIterable<Document> documents = news_collection.find();
		MongoCursor<Document> cursor = documents.iterator();

		// Variable to count keywords frequency in different news articles
		int canada_frequency = 0;
		int vancouver_frequency = 0;
		int toronto_frequency = 0;

		while (cursor.hasNext()) {
			Document document = cursor.next();
			JSONObject object = new JSONObject(document);
			// Get news body text from single document
			String news_body = object.getString("body").trim();
			System.out.println(news_body);
			if (news_body.contains("Canada") || news_body.contains("canada")) {
				canada_frequency++;
			} else if (news_body.contains("Vancouver") || news_body.contains("vancouver")) {
				vancouver_frequency++;
			} else if (news_body.contains("Toronto") || news_body.contains("toronto")) {
				toronto_frequency++;
			}
		}

		format.format("|%-15s|%-30s|%-20s|%-15s|\n", "Canada", canada_frequency, n, Math.log10(n / canada_frequency));
		format.format("|%-15s|%-30s|%-20s|%-15s|\n", "Halifax", vancouver_frequency, n,
				Math.log10(n / vancouver_frequency));
		format.format("|%-15s|%-30s|%-20s|%-15s|\n", "Toronto", canada_frequency, n, Math.log10(n / toronto_frequency));
		format.format("%86s\n", "-".repeat(86));
		System.out.println(format);
	}
}
