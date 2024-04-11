package com.ballsquad.ballsquad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.ballsquad.ballsquad.model.Author;
import com.ballsquad.ballsquad.model.Work;
import com.ballsquad.ballsquad.repo.AuthorRepo;
import com.ballsquad.ballsquad.repo.WorkRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
public class AppController {
    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private WorkRepo workRepo;

    @GetMapping("/getAuthor")
    public ResponseEntity<String> getAuthor(@RequestParam String name) {
        try {
            // Check if author exists in the database
            Author author = authorRepo.findByName(name);

            if (author == null) {
                // If author doesn't exist, make a request to Open Library API to search for the author
                String apiUrl = "https://openlibrary.org/search/authors.json?q=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString());

                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(apiUrl, String.class);
                
                JSONObject jsonObject = new JSONObject(result);
                int numFound = jsonObject.getInt("numFound");

                if (numFound > 0) {
                    // If author is found, extract author name and ID from the API response
                    JSONObject docs = jsonObject.getJSONArray("docs").getJSONObject(0);
                    String authorName = docs.getString("name");
                    String authorId = docs.getString("key");

                    // Create a new Author object and save it to the database
                    author = new Author(authorName, authorId);
                    authorRepo.save(author);

                    // Return the author information as a JSON response
                    return ResponseEntity.ok(String.format("{\"author_name\": \"%s\", \"author_id\": \"%s\"}", author.getName(), author.getId()));
                } else {
                    // If author is not found, return a not found error
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Author not found\"}");
                }
            } else {
                // If author exists in the database, return the author information from the database
                return ResponseEntity.ok(String.format("{\"author_name\": \"%s\", \"author_id\": \"%s\"}", author.getName(), author.getId()));
            }
        } catch (Exception e) {
            // If an error occurs, return an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An error occurred while processing your request\"}");
        }
    }


    @GetMapping("/getWorks")
    public ResponseEntity<String> getWorks(@RequestParam String authorId) {
        try {
            // Check if author exists in the database
            Author author = authorRepo.findById(authorId).orElse(null);

            if (author == null) {
                // If author doesn't exist, return a not found error
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Author not found\"}");
            } 

            // Get works by author from the database
            List<Work> works = workRepo.findByAuthor(author.getName());

            if (works.isEmpty()) {
                // If works are not found in the database, make a request to Open Library API to get works by author
                String apiUrl = "https://openlibrary.org/authors/" + authorId + "/works.json";

                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(apiUrl, String.class);
                
                JSONObject jsonObject = new JSONObject(result);
                JSONArray entries = jsonObject.getJSONArray("entries");

                if (entries.length() > 0) {
                    // If works are found in the API response, save them to the database
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject workJson = entries.getJSONObject(i);
                        String workTitle = workJson.getString("title");
                        String workId = workJson.getString("key");

                        Work work = new Work(workTitle, workId, author.getName());
                        workRepo.save(work);
                        works.add(work);
                    }
                } else {
                    // If works are not found in the API response, return a not found error
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Works not found\"}");
                }
            }

            // Convert works to JSON format and return as a response
            JSONArray worksJson = new JSONArray();
            for (Work work : works) {
                JSONObject workJson = new JSONObject();
                workJson.put("work_title", work.getTitle());
                workJson.put("work_id", work.getId());
                worksJson.put(workJson);
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("works", worksJson);

            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            // If an error occurs, return an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An error occurred while processing your request\"}");
        }
    }
}
