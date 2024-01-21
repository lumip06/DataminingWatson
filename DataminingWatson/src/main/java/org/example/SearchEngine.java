package org.example;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class SearchEngine {
    private IndexSearcher searcher = null;
    private QueryParser parser = null;

    public SearchEngine(String[] fields) {
        try {
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("wiki-index").toPath())));
            parser = new MultiFieldQueryParser(fields, new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Oopps! :( something went wrong!");
        }
    }

    public TopDocs performSearch(String queryString, int i) throws ParseException, IOException {
        Query query = parser.parse(queryString);
        return searcher.search(query, i);
    }

    public Document getDocument(int docid) throws IOException {
        return searcher.doc(docid);
    }
}