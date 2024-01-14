package org.example;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
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

    /**
     * Constructor for the Search Engine
     *
     * @throws IOException
     */
    public SearchEngine() throws IOException {
        try {
            // give the searcher the index path to look into
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("wiki-index").toPath())));

            // which searchable field to look for
            parser = new QueryParser("Content", new StandardAnalyzer());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Oopps! :( something went wrong!");
        }
    }

    /**
     * Search for a specific query string.
     *
     * @param queryString
     *            a query from the user
     * @param i
     *            number of top documents to be retrieved
     * @return a list of Top documents retrieved
     * @throws ParseException
     * @throws IOException
     */
    public TopDocs performSearch(String queryString, int i) throws ParseException, IOException {
        Query query = parser.parse(queryString);
        return searcher.search(query, i);
    }

    /**
     * get a specific document from the list of top documents retrieved.
     *
     * @param docid
     *            documents ID that is needed
     * @return Matching document
     * @throws IOException
     */
    public Document getDocument(int docid) throws IOException {
        return searcher.doc(docid);
    }
}