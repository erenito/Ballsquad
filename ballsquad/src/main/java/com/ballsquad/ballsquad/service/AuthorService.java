package com.ballsquad.ballsquad.service;

import com.ballsquad.ballsquad.model.Author;
import com.ballsquad.ballsquad.repo.AuthorRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepo authorRepo;

    public Author getOrCreateAuthor(String name) {
        Author author = authorRepo.findByName(name);
        return handleAuthorSearch(author, name, false);
    }

    public Author getOrCreateAuthorById(String authorId) {
        Author author = authorRepo.findById(authorId).orElse(null);
        return handleAuthorSearch(author, authorId, true);
    }

    private Author handleAuthorSearch(Author author, String searchParameter, boolean isIdSearch) {
        if (author == null) {
            try {
                // Construct the API URL based on the search parameter and search type
                String apiUrl = isIdSearch
                    ? "https://openlibrary.org/authors/" + searchParameter + ".json"
                    : "https://openlibrary.org/search/authors.json?q=" + URLEncoder.encode(searchParameter, StandardCharsets.UTF_8.toString());
                
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(apiUrl, String.class);
                
                JSONObject jsonObject = new JSONObject(result);
                
                if (isIdSearch) {
                    // If searching by ID, check if the author name exists in the response
                    if (jsonObject.has("name")) {
                        String authorName = jsonObject.getString("name");
                        author = new Author(authorName, searchParameter);
                    } else {
                        throw new RuntimeException("Author not found with ID: " + searchParameter);
                    }
                } else {
                    // If searching by name, check if any authors are found in the response
                    int numFound = jsonObject.getInt("numFound");
                    if (numFound > 0) {
                        JSONObject docs = jsonObject.getJSONArray("docs").getJSONObject(0);
                        String authorName = docs.getString("name");
                        String authorId = docs.getString("key");
                        author = new Author(authorName, authorId);
                    } else {
                        throw new RuntimeException("Author not found with name: " + searchParameter);
                    }
                }
                authorRepo.save(author);
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch or create author", e);
            }
        }
        return author;
    }
    
}
