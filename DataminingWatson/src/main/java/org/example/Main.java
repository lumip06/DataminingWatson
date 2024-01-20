package org.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.example.wiki_article.ArticleIndexer;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    static int maxDocNoToRetrieve = 10;
    static String questionsPath = "src/main/java/org/example/questions/questions.txt";

    public static void main(String ars[]) {
        String op;
        boolean exit = false;

        do {
            System.out.println("\n\n******************************************************");
            System.out.println("IBM’s Watson Project");
            System.out.println("******************************************************");
            System.out.println("[1]. Create index");
            System.out.println("[2]. Run query");
            System.out.println("[3]. Run questions");
            System.out.println("[4]. Set max number of retrieved documents  (>0  - Default 10)");
            System.out.println("[0]. Exit");
            System.out.println("Enter a number: ");

            Scanner in = new Scanner(System.in);
            op = in.nextLine();

            switch (op) {
                case "0" -> {
                    System.out.println("System Exiting...Bye!");
                    exit = true;
                }
                case "1" -> createIndex();
                case "2" -> runQuery();
                case "3" -> runQuestions();
                case "4" -> setTopDocNum();
                default -> {
                }
            }
        } while (!exit);
    }

    private static void createIndex() {
        try {
            System.out.println("**********************");
            System.out.println("* Creating Indexes *");
            System.out.println("**********************");

            long startTime = System.nanoTime();
            ArticleIndexer indexer = new ArticleIndexer();
            indexer.buildIndexes();
            long estimatedTime = System.nanoTime() - startTime;
            double seconds = (double) estimatedTime / 1000000000.0;

            System.out.println("(Elapsed Time : " + seconds + " Seconds)");
            System.out.println("****************************");
            System.out.println("* Creating Indexes DONE! *");
            System.out.println("****************************");
        } catch (IOException | XMLStreamException e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        }
    }

    private static void runQuery() {
        System.out.println("Enter a search query: ");

        Scanner in = new Scanner(System.in);
        String query = in.nextLine();
        
        try {
            System.out.println("Performing search");

            long startTime = System.nanoTime();
            SearchEngine se = new SearchEngine();
            TopDocs td = se.performSearch(query, maxDocNoToRetrieve);
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

    public static void runQuestions() {
        try(BufferedReader reader = new BufferedReader(new FileReader(questionsPath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String category = line.trim().replace("-", "\\-").replace("!", "\\!");

                if ((line = reader.readLine()) != null) {
                    String body = line.trim().replace("-", "\\-").replace("!", "\\!");

                    if ((line = reader.readLine()) != null) {
                        String expectedResult = line.trim().replace("-", "\\-").replace("!", "\\!");
                        runSingleQuery(category, body, expectedResult);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Error reading questions file", e);
        }
    }

    public static void runSingleQuery(String category, String body, String expectedResult) throws IOException, ParseException {
        String query = "Body:\"" + body + "\" OR Category:\"" + category + "\"";

        SearchEngine searchEngine = new SearchEngine();
        TopDocs topDocs = searchEngine.performSearch(query, maxDocNoToRetrieve);
        ScoreDoc[] hits = topDocs.scoreDocs;

        boolean perfectHitFound = false;

        System.out.println("\n----------------------------------------------------------");

        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document document = searchEngine.getDocument(docId);
            System.out.println((i + 1) + ". \t" + document.get("Title"));

            if(Objects.equals(document.get("Title"), expectedResult)) {
                perfectHitFound = true;
            }
        }
        if(hits.length > 0) {
            System.out.println("");
        }
        System.out.println("** Found " + hits.length + " hits.");
        System.out.println("** Perfect hit found: " + perfectHitFound + "\n");

        System.out.println("** Content: " + body);
        System.out.println("** Expected result: " + expectedResult);
        System.out.println("----------------------------------------------------------");
    }

    private static void setTopDocNum() {
        System.out.println("Enter a number: ");

        Scanner in = new Scanner(System.in);
        int op = in.nextInt();

        if(op > 0) {
            maxDocNoToRetrieve = op;
        }
    }

    public static void prettyPrint(ScoreDoc[] hits, SearchEngine se) {
        String id, link, title, desc;
        float score;
        try {
            System.out.println("-----------------------------");
            System.out.println("| Search Result Top " + maxDocNoToRetrieve + " found |");
            System.out.println("-----------------------------");

            for (int i = 0; i < hits.length; i++) {
                Document doc = se.getDocument(hits[i].doc);
                score = hits[i].score;
                id = (i + 1) + ". \t" + doc.get("Title") + "\t( " + score + " )";
                System.out.println(id);

//                Document doc = se.getDocument(hits[i].doc);
//
//                score = hits[i].score;
////                id = "| Article " + doc.get("Title") + "\t( " + score + ") |";
//                id = "| " + doc.get("Title") + "\t( " + score + ") |";
//                title = "| " + doc.get("Title");
//                desc = "| -> " + doc.get("category");
//                link = "https://ro.wikipedia.org/wiki/" + doc.get("Title");
//                link = link.replace(" ", "_");
//                link = "| " + link;
//
////                System.out.println("");
//                for (int x = 0; x <= id.length() + 2; x++) {
//                    System.out.print("-");
//                }
////                System.out.println("");
//                System.out.println(id);
//                for (int x = 0; x <= id.length() + 2; x++) {
//                    System.out.print("-");
//                }
//                System.out.println("");
//
////                System.out.println(title);
//
////                System.out.println(desc);
//
////                System.out.println(link);
            }
        } catch (Exception e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        }
    }
}