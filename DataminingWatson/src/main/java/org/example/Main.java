package org.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.example.wiki_article.ArticleIndexer;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    static boolean exit = false, options = false;
    static int ret = 10;
    static String query = null;

    public Main() {
    }

    /**
     * Main method to run the whole program.<br>
     * Takes the user input and dispatches it accordingly.
     *
     * @param ars
     *            argument parameters. (not needed)
     */
    public static void main(String ars[]) {
        do {
            System.out.println("\n\n******************************************************");
            System.out.println("Welcome To Wikipedia offline *Mini* Search Engine V1.0");
            System.out.println("******************************************************");
            System.out.println("For the system options, press OP anytime");
            System.out.println("To exit, press Q anytim");
            System.out.println("OR! Enter a search query: ");

            // wait for user input
            Scanner in = new Scanner(System.in);
            query = in.nextLine();

            // dispatch user inout
            switch (query) {
                case "OP":
                    options = true;
                    moreOptions();
                    options = false;
                    query = null;
                    break;
                case "op":
                    options = true;
                    moreOptions();
                    options = false;
                    query = null;
                    break;
                case "Q":
                    System.out.println("System Exiting...Bye!");
                    exit = true;
                    break;
                case "q":
                    System.out.println("System Exiting...Bye!");
                    exit = true;
                    options = false;
                    break;
                case "":
                    break;
                default:
                    searchQuery(query);
                    break;
            }
        } while (!exit);
    }

    /**
     * Runs if "OP" / "op" was invoked by the user to customize options of the
     * server.
     */
    private static void moreOptions() {
        int op;
        String[] args = {};

        System.out.println("\n\n********************");
        System.out.println("Customizable Options");
        System.out.println("********************");
        System.out.println("0. Go Back");
        System.out.println("1.Redo data preprocessing & Indexing");
        System.out.println("2.Reindex wikipedia Articles");
        System.out.println("Or, Set max number of retrieved documents  (> 4 - Default 10)");
        System.out.println("Enter a number: ");

        // wait for user input
        Scanner in = new Scanner(System.in);
        op = in.nextInt();

        // dispatch user input
        do {
            switch (op) {
                case 0:
                    options = false;
                    break;
                case 1:
                    preProcessData();
                    reIndexAll();
                    options = false;
                    break;
                case 2:
                    reIndexAll();
                    options = false;
                    break;
                default:
                    topDocNum(op);
                    options = false;
                    break;
            }
        } while (options);
    }

    /**
     * Rebuild the FULL version of the index. (always overwrite the old)
     */
    private static void reIndexAll() {
        try {
            System.out.println("**********************");
            System.out.println("* Rebuilding Indexes *");
            System.out.println("**********************");

            // reindex
            long startTime = System.nanoTime();
            ArticleIndexer indexer = new ArticleIndexer();
            indexer.rebuildIndexes();
            long estimatedTime = System.nanoTime() - startTime;
            double seconds = (double) estimatedTime / 1000000000.0;

            System.out.println("(Elapsed Time : " + seconds + " Seconds)");
            System.out.println("****************************");
            System.out.println("* Rebuilding Indexes DONE! *");
            System.out.println("****************************");
        } catch (IOException e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        } catch (XMLStreamException e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        }
    }

    /**
     * Searches a user query against the current dataset index.
     *
     * @param q
     *            Query string from the user
     */
    private static void searchQuery(String q) {
        try {
            System.out.println("Performing search");

            // SEARCH
            long startTime = System.nanoTime();
            SearchEngine se = new SearchEngine();
            TopDocs td = se.performSearch(q, ret);
            long estimatedTime = System.nanoTime() - startTime;
            double seconds = (double) estimatedTime / 1000000000.0;

            System.out.println("Results Found (Elapsed Time: " + seconds + " Seconds)");
            ScoreDoc[] hits = td.scoreDocs;

            prettyPrint(hits, se);
        } catch (Exception e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        }
    }

    /**
     * Redo pre-processing steps and also reindex after.
     */
    private static void preProcessData() {
        String[] args = {};
        TextToXml.main(args);
    }

    /**
     * Changes the default number of the documents retrieved (10).
     *
     * @param i
     *            the new number
     */
    private static void topDocNum(int i) {
        ret = i;
    }

    /**
     * Pretty output the final search results.
     *
     * @param hits
     *            documents retrieved that have the top hits.
     * @param se
     *            A search engine instance.
     */
    public static void prettyPrint(ScoreDoc[] hits, SearchEngine se) {
        String id, link, title, desc;
        float score;
        try {
            System.out.println("-----------------------------");
            System.out.println("| Search Result Top " + ret + " found |");
            System.out.println("-----------------------------");

            for (int i = 0; i < hits.length; i++) {
                Document doc = se.getDocument(hits[i].doc);

                score = hits[i].score;
                id = "| Article " + doc.get("ID") + "\t( " + Float.toString(score) + ") |";
                title = "| " + doc.get("Title");
                desc = "| -> " + doc.get("Description");
                link = "| " + doc.get("Link");

                System.out.println("");
                for (int x = 0; x <= id.length() + 2; x++) {
                    System.out.print("-");
                }
                System.out.println("");
                System.out.println(id);
                for (int x = 0; x <= id.length() + 2; x++) {
                    System.out.print("-");
                }
                System.out.println("");

                System.out.println(title);

                System.out.println(desc);

                System.out.println(link);
            }
        } catch (Exception e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        }
    }
}
