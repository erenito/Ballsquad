package com.ballsquad.ballsquad.service;

import com.ballsquad.ballsquad.model.Author;
import com.ballsquad.ballsquad.model.Work;
import com.ballsquad.ballsquad.repo.WorkRepo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class WorkService {

    @Autowired
    private WorkRepo workRepo;

    public List<Work> getOrCreateWorks(Author author, String authorId) {
        List<Work> works = workRepo.findByAuthor(author.getName());
        if (works.isEmpty()) {
            try {
                // Construct the API URL using the authorId parameter
                String apiUrl = "https://openlibrary.org/authors/" + authorId + "/works.json";
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(apiUrl, String.class);

                JSONObject jsonObject = new JSONObject(result);
                JSONArray entries = jsonObject.getJSONArray("entries");

                for (int i = 0; i < entries.length(); i++) {
                    JSONObject workJson = entries.getJSONObject(i);
                    String workTitle = workJson.getString("title");
                    String workId = workJson.getString("key");

                    Work work = new Work(workTitle, workId, author.getName());
                    workRepo.save(work);
                    works.add(work);
                }
            } catch (Exception e) {
                // If an exception occurs during the API request or work creation, throw a runtime exception
                throw new RuntimeException("Failed to fetch or create works", e);
            }
        }
        return works;
    }
}
