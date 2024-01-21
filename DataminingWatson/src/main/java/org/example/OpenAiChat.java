package org.example;
import org.apache.lucene.search.ScoreDoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.json.*;


public class OpenAiChat {
    private String openaiApiKey = "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";
    public OpenAiChat() {
    }

    public ArrayList<String> reorder(SearchEngine searchEngine, ArrayList<ScoreDoc> answers, String question) {
        try {
            String prompt = "Act a query retriever in order to answer best the question: ${question}. Order the following wikipedia document answers: ${answers}. Use ';' as a separators";
            prompt = prompt.replace("${question}", question);
            prompt = prompt.replace("${answers}", answers.stream().map(hit-> {
                        try {
                            return searchEngine.getDocument(hit.doc).get("Title");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.joining("; ")));
            // Set up the API endpoint
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + openaiApiKey);

            // Enable input/output streams
            connection.setDoOutput(true);

            // Construct the request payload
            String requestBody = "{\"model\":\"gpt-3.5-turbo-1106\",\"messages\": [{\"role\": \"user\", \"content\": \""+prompt+"\"}], \"max_tokens\":150}";
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);

            // Write the payload to the connection
            try (OutputStream os = connection.getOutputStream()) {
                os.write(input, 0, input.length);
            }

            // Get the response from the API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JSONObject jsonObj = new JSONObject(response.toString());
                    JSONArray choices = jsonObj.getJSONArray("choices");
                    String message = choices.getJSONObject(0).getJSONObject("message").getString("content");
                    return new ArrayList<String>(Arrays.asList(message.split(";")));
                }
            } else {
                System.out.println("Error: " + connection.getResponseCode()+" "+connection.getResponseMessage());
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
