package com.ballsquad.ballsquad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.ballsquad.ballsquad.model.Author;
import com.ballsquad.ballsquad.model.Work;
import com.ballsquad.ballsquad.service.AuthorService;
import com.ballsquad.ballsquad.service.WorkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;


@RestController
public class AppController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private WorkService workService;

    @GetMapping("/getAuthor")
    public ResponseEntity<String> getAuthor(@RequestParam String name) {
        try {
            Author author = authorService.getOrCreateAuthor(name);
            if (author == null) {
                // If author is not found, return a not found response
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Author not found\"}");
            }
            // Return the author information in JSON format
            return ResponseEntity.ok(String.format("{\"author_name\": \"%s\", \"author_id\": \"%s\"}", author.getName(), author.getId()));
        } catch (Exception e) {
            // If an error occurs, return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An error occurred while processing your request\"}");
        }
    }

    @GetMapping("/getWorks")
    public ResponseEntity<String> getWorks(@RequestParam String authorId) {
        try {
            Author author = authorService.getOrCreateAuthorById(authorId);
            if (author == null) {
                // If author is not found, return a not found response
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Author not found\"}");
            }
            // Get or create the works of the author
            List<Work> works = workService.getOrCreateWorks(author, authorId);
            JSONArray worksJson = new JSONArray();
            for (Work work : works) {
                // Create a JSON object for each work and add it to the works JSON array
                JSONObject workJson = new JSONObject();
                workJson.put("work_title", work.getTitle());
                workJson.put("work_id", work.getId());
                worksJson.put(workJson);
            }
            // Create a JSON object for the response and add the works JSON array to it
            JSONObject responseJson = new JSONObject();
            responseJson.put("works", worksJson);
            // Return the response JSON in string format
            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            // If an error occurs, return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An error occurred while processing your request\"}");
        }
    }
}
