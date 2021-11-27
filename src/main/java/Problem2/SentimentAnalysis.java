package Problem2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

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
	public static void main(String args[]) throws URISyntaxException, IOException {
		// MongoDB Configurations
		ConnectionString connectionString = new ConnectionString(
				"mongodb+srv://root:root@cluster0.jvs2l.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase cleaned_database = mongoClient.getDatabase("ProcessedDb");

		// All collections
		MongoIterable<String> collections = cleaned_database.listCollectionNames();

		// Formatting the table
		Formatter format = new Formatter();
		format.format("%106s\n", "-".repeat(60));
		format.format("|%15s|%15s|%50s|%10s|%10s|\n","News Article","News Content","Matched Words","Overall Score","Polarity");
		// Collection wise operation
		for (String collection : collections) {
			// Temporary collection
			MongoCollection current_collection = cleaned_database.getCollection(collection);
			FindIterable<Document> documents = current_collection.find();
			MongoCursor<Document> cursor = documents.iterator();

			int article_count = 1;
			// Iterating over all documents inside a single collection
			while (cursor.hasNext()) {
				Document document = cursor.next();
				JSONObject object = new JSONObject(document);
				// Get Tweet text from single document
				String tweet = object.getString("full_text");
//				System.out.println(tweet);
				// To count bag of words
				Map<String, Integer> bag_of_words = new HashMap<String, Integer>();
				String[] words = tweet.split(" ");
				// For each loop for all words in bag of words
				for (String word : words) {
					if (bag_of_words.containsKey(word)) {
						bag_of_words.put(word, bag_of_words.get(word) + 1);
					} else {
						bag_of_words.put(word, 1);
					}
				}

				// Get all words from negative words file
				URL negative_words_url = SentimentAnalysis.class.getClassLoader().getResource("negative-words.txt");
//				BufferedReader br = new BufferedReader(new FileReader(new File(negative_words_url.toURI())));
//				Path path = Paths.get(negative_words_url.toURI().getPath());
				File negative_file = new File(negative_words_url.toURI());
				Path negative_path = Paths.get(negative_file.getAbsolutePath());
				List<String> negative_words = Files.readAllLines(negative_path);

				// Get all words from positive words file
				URL positive_words_url = SentimentAnalysis.class.getClassLoader().getResource("positive-words.txt");
				File positive_file = new File(negative_words_url.toURI());
				Path positive_path = Paths.get(positive_file.getAbsolutePath());
				List<String> positive_words = Files.readAllLines(positive_path);

				// List for words that has been matched with either of words
				ArrayList<String> matched_words = new ArrayList<String>();
				// To calculate polarity of tweet
				int overall_score = 0;

				// Calculate polarity of tweet
				for (String word : words) {
					if (negative_words.contains(word)) {
						overall_score--;
						matched_words.add(word);
					} else if (positive_words.contains(word)) {
						overall_score++;
						matched_words.add(word);
					}
				}
				
				//Setting up polarity of news article
				String polarity;
				if(overall_score<0) {
					polarity="Negative"; 
				}
				else if(overall_score>0)
				{
					polarity="Positive";
				}
				else
				{
					polarity="Neutral";
				}
				
				format.format("|%15s|%15s|%50s|%10s|%10s|\n",article_count,tweet,matched_words.toString(),overall_score,polarity);
				article_count++;
			}
		}
		System.out.println(format);

	}
}
