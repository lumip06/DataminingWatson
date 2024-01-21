package org.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.example.wiki_article.ArticleIndexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static final String questionsPath = "src/main/java/org/example/questions/questions.txt";
    private static int maxDocNoToRetrieve = 10;
    private static int hitsFound;

    public static void main(String[] args) {
        String option;
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
            option = in.nextLine();

            switch (option) {
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
            indexer.createIndex();

            long estimatedTime = System.nanoTime() - startTime;
            double seconds = (double) estimatedTime / 1000000000.0;

            System.out.println("\n----------------------------------------------------------");
            System.out.println("(Elapsed Time : " + seconds + " Seconds)");
            System.out.println("****************************");
            System.out.println("* Creating Indexes DONE! *");
            System.out.println("****************************");
        } catch (IOException e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        }
    }

    private static void runQuery() {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter the question category (press enter for no category): ");
        String category = in.nextLine();

        System.out.println("Enter a search query: ");
        String clue = in.nextLine();
        
        try {
            System.out.println("\n");
            System.out.println("* Performing search...");

            long startTime = System.nanoTime();

            String[] fields;
            String queryString;
            if(Objects.equals(category, "")) {
                fields = new String[]{"Body"};
                queryString = clue;
            } else {
                fields = new String[]{"Body", "Category"};
                queryString = "(" + clue + ") OR (" + category + ")";
            }

            SearchEngine se = new SearchEngine(fields);
            TopDocs td = se.performSearch(queryString, maxDocNoToRetrieve);

            long estimatedTime = System.nanoTime() - startTime;
            double seconds = (double) estimatedTime / 1000000000.0;

            System.out.println("* Results Found! (Elapsed Time: " + seconds + " Seconds)\n");
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
            Pattern pattern = Pattern.compile("[.,:;!?-]");
            hitsFound = 0;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String category = line.trim().replaceAll(pattern.pattern(), "");
                line = reader.readLine();
                String clue = line.trim().replaceAll(pattern.pattern(), "");
                line = reader.readLine();
                String expectedResult = line.trim();
                runSingleQuery(category, clue, expectedResult);
            }
            System.out.println("\n");
            System.out.println("** Hits found: " + hitsFound + "/100");
            System.out.println("\n");
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Error reading questions file", e);
        }
    }

    public static void runSingleQuery(String category, String clue, String expectedResult) throws IOException, ParseException {
        category = category.replace("(", "");
        category = category.replace(")", "");

        String queryString = "(" + clue + ") OR (" + category + ")";

        String[] fields = new String[]{"Body", "Category"};
        SearchEngine searchEngine = new SearchEngine(fields);
        TopDocs topDocs = searchEngine.performSearch(queryString, maxDocNoToRetrieve);
        ScoreDoc[] hits = topDocs.scoreDocs;

        boolean perfectHitFound = false;

        System.out.println("\n----------------------------------------------------------");

        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document document = searchEngine.getDocument(docId);
            System.out.println((i + 1) + ". \t" + document.get("Title"));

            if(Objects.equals(document.get("Title"), expectedResult)) {
                perfectHitFound = true;
                hitsFound++;
            }
        }
        if(hits.length > 0) {
            System.out.println("");
        }
        System.out.println("** Found " + hits.length + " hits.");
        System.out.println("** Perfect hit found: " + perfectHitFound + "\n");

        System.out.println("** Clue: " + clue);
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
        String id;
        float score;
        try {
            System.out.println("-----------------------------");
            System.out.println("| Search Result Top " + maxDocNoToRetrieve + " found |");
            System.out.println("-----------------------------");

            for (int i = 0; i < hits.length; i++) {
                Document doc = se.getDocument(hits[i].doc);
                score = hits[i].score;
                id = (i + 1) + ". \t" + doc.get("Title") + "\t(score: " + score + " )";
                System.out.println(id);
            }
        } catch (Exception e) {
            System.out.println("Oopps! :( something went wrong!");
            e.printStackTrace();
        }
    }
}