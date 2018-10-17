### CS7IS3 Assignment 1: Lucene and Cranfield

*createIndex.jar*: creates an index of the cran collection, and stores in disk.

To execute this file, run the following command:<br/>
```java -jar createIndex.jar```

*queryIndex.jar*: searches through the index, and returns results. The output results are stored in output directory.

To execute this file, run the following command:<br/>
```java -jar queryIndex.jar```

The files present in output directory, namely QRels.txt and results.txt can be used to evaluate metrics using TREC Eval tool.<br/>

The command is as follows:<br/>
```../trec_eval output/QRels.txt output/results.txt```


To change the similarity method:<br/>
In the createIndex file replace the string input in this line of
code to the preferred similarity:
```IndexWriterConfig config = createIndex(analyzer, ”enter here”);```

