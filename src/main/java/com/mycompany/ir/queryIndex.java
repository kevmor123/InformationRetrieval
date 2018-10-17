package com.mycompany.ir;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.*;

public class queryIndex {

	// the location of the search index
	private static String INDEX_DIRECTORY = "../Test";
	// Limit the number of search results we get
	private static int MAX_RESULTS = 30;

	public static void main(String[] args) throws IOException, ParseException {

		CharArraySet stopwords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		Analyzer analyzer = new tokenizeAnalyser(stopwords);

		// Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		// create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		isearcher.setSimilarity(new BM25Similarity());
		Map<String, Float> weight = weight();

		MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]
		 {"title", "author", "bibl", "content"}, analyzer, weight);

		String queryText = "";
		int tmp = 1;
		for (String arg : args) {
			// Load the contents of the file
			System.out.printf("Indexing \"%s\"\n", arg);
			BufferedReader in = Files.newBufferedReader(Paths.get(arg), StandardCharsets.UTF_8);
			String line = in.readLine();
	    int count = 1;
			String file = "";
	    while (line != null) {

				queryText = "";
				queryText = queryText.trim();
				if(line.substring(0,2).equals(".I")){
					//System.out.println(".I " + count);
					count = count + 1;
					line = in.readLine();
					if(line.substring(0,2).equals(".W")){
						line = in.readLine();
						//System.out.println("line: " + line);
						while(!(line.substring(0,2).equals(".I"))){
							queryText = queryText + " " + line;
							line = in.readLine();
							if(line == null){
								break;
							}
						}
					}
				}

				Query query = parser.parse(QueryParser.escape(queryText));
				// Get the set of results
				ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;
				// Print the results
				//	System.out.println("Documents: " + hits.score);
				int rank = 1;
				String text = "";
				for (int i = 0; i < hits.length; i++) {
					Document hitDoc = isearcher.doc(hits[i].doc);
					if(hits[i].score > 0){
						text = tmp + " 0 " + hitDoc.get("index") + " " +  rank + " " + hits[i].score+ " EXP " + "\n";
					//	System.out.print(text);
						rank = rank +1;
						file = file + text;
					}
				}
				writeToFile(file);
				tmp= tmp + 1;
	   }
		}
		ireader.close();
		directory.close();
	}

	public static Map<String, Float> weight(){
		Map<String, Float> weigthingMap = new HashMap();
		weigthingMap.put("title", (float) 0.34);
		weigthingMap.put("author", (float) 0.01);
		weigthingMap.put("bibl", (float) 0.02);
		weigthingMap.put("content", (float) 0.62);
		return weigthingMap;
	}

	public static void writeToFile(String text) {
		try {
			File file = new File("Output/results.txt");
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(text);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
}
