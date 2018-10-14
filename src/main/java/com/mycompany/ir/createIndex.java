package com.mycompany.ir;

import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.nio.file.Files;
import org.apache.lucene.analysis.CharArraySet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.*;

public class createIndex {

	// Directory where the search index will be saved
	private static String INDEX_DIRECTORY = "/home/kevin/MSC/IR/InformationRetrieval/Test";

	public static void main(String[] args) throws IOException {
		// Make sure we were given something to index
		if (args.length <= 0) {
            System.out.println("Expected corpus as input");
            System.exit(1);
        }

			CharArraySet stopwords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
			// Analyzer that is used to process TextField
			Analyzer analyzer = new tokenizeAnalyser(stopwords);
		// ArrayList of documents in the corpus
			ArrayList<Document> documents = new ArrayList<Document>();
			// Open the directory that contains the search index
			Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
  		// Set up an index writer to add process and save documents to the index
  		IndexWriterConfig config =  createIndex(analyzer, "BM25");
  		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
  		IndexWriter iwriter = new IndexWriter(directory, config);

  		for (String arg : args) {
  			// Load the contents of the file
  			System.out.printf("Indexing \"%s\"\n", arg);
  			InputStream stream = Files.newInputStream(Paths.get(arg));
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        String temp = br.readLine();
        Document doc;
        String docType = "";
				String index = "index";
				int count = 0;
        while(temp != null){
          if(temp.substring(0,2).equals(".I")){
            doc = new Document();
						count++;
						doc.add(new StringField("index", Integer.toString(count) , Field.Store.YES));
            Field pathField = new StringField("path", temp, Field.Store.YES);
            doc.add(pathField);
            temp = br.readLine();
            while(!(temp.substring(0,2).equals(".I"))){
              if(temp.substring(0,2).equals(".T")){
                  docType = "title";
                  temp = br.readLine();
              } else if(temp.substring(0,2).equals(".A")){
                  docType = "author";
                  temp = br.readLine();
              } else if(temp.substring(0,2).equals(".B")){
                    docType = "bibl";
                    temp = br.readLine();
              } else if(temp.substring(0,2).equals(".W")){
                      docType = "content";
                      temp = br.readLine();
              }
              doc.add(new TextField(docType, temp, Field.Store.YES));
              temp = br.readLine();
              if(temp == null){
                break;
              }
            }
  					// Add the file to our linked list
  					documents.add(doc);
  				}
    		}
  		}
  		// Write all the documents in the linked list to the search index
  		iwriter.addDocuments(documents);

  		// Commit everything and close
  		iwriter.close();
  		directory.close();
  	}

		private static IndexWriterConfig createIndex(Analyzer analyzer, String rank){
				IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			switch (rank) {
			 case "BM25":
			  return indexWriterConfig.setSimilarity(new BM25Similarity());
			 case "Boolean":
			  return indexWriterConfig.setSimilarity(new BooleanSimilarity());
			 case "Classic":
			  return indexWriterConfig.setSimilarity(new ClassicSimilarity());
			 case "lm_dirichlet":
			  return indexWriterConfig.setSimilarity(new LMDirichletSimilarity());
			 default:
			  return null;
			}
		}
}
