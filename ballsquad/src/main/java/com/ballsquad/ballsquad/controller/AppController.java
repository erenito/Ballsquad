package com.ballsquad.ballsquad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.ballsquad.ballsquad.model.Author;
import com.ballsquad.ballsquad.model.Work;
import com.ballsquad.ballsquad.repo.AuthorRepo;
import com.ballsquad.ballsquad.repo.WorkRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.JSONObject; 
import java.util.List;

@RestController
public class AppController {
    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private WorkRepo workRepo;

    @GetMapping("/getAuthor")
    public String getAuthor(@RequestParam String name) {
        String responseJson;

        Author author = authorRepo.findByName(name);

        if(author == null) {
            try {
                String apiUrl = "https://openlibrary.org/search/authors.json?q=" + name.replace(" ", "+");

                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(apiUrl, String.class);
                
                JSONObject jsonObject = new JSONObject(result);
                int numFound = jsonObject.getInt("numFound");

                if(numFound > 0) {
                    JSONObject docs = jsonObject.getJSONArray("docs").getJSONObject(0);
                    String authorName = docs.getString("name");
                    String authorId = docs.getString("key");

                    author = new Author(authorName, authorId);
                    authorRepo.save(author);

                    responseJson = String.format("{\"author_name\": \"%s\", \"author_id\": \"%s\"}", author.getName(), author.getId());
                } else {
                    responseJson = "{\"error\": \"Author not found\"}";
                }
            } catch(Exception e) {
                responseJson = "{\"error\": \"An error occurred while processing your request\"}";
            }
        } else {        
            responseJson = String.format("{\"author_name\": \"%s\", \"author_id\": \"%s\"}", author.getName(), author.getId());
        }

        return responseJson;
    }

    @GetMapping("/getWorks")
    public String getWorks(@RequestParam String authorId) {
        String responseJson;

        Author author = authorRepo.findById(authorId).orElse(null);

        if(author == null) {
            responseJson = "{\"error\": \"Author not found\"}";
        } 
        else {

            List<Work> works = workRepo.findByAuthor(author.getName());

            if(works == null) {
                try {
                    String apiUrl = "https://openlibrary.org/authors/" + authorId + "/works.json";

                    RestTemplate restTemplate = new RestTemplate();
                    String result = restTemplate.getForObject(apiUrl, String.class);
                    
                    JSONObject jsonObject = new JSONObject(result);
                    int numFound = jsonObject.getInt("works_count");

                    if(numFound > 0) {



                        for(int i = 0; i < jsonObject.getJSONArray("entries").length(); i++) {
                            JSONObject workJson = jsonObject.getJSONArray("entries").getJSONObject(i);
                            String workTitle = workJson.getString("title");
                            String workId = workJson.getString("key");

                            Work work = new Work(workTitle, workId, author.getName());
                            workRepo.save(work);
                            works.add(work);
                        }

                        responseJson = "{\"works\": [";
                        for(int i = 0; i < works.size(); i++) {
                            responseJson += String.format("{\"work_title\": \"%s\", \"work_id\": \"%s\"}", works.get(i).getTitle(), works.get(i).getId());
                            if(i < works.size() - 1) {
                                responseJson += ",";
                            }
                        }
                    } else {
                        responseJson = "{\"error\": \"Works not found\"}";
                    }
                } catch(Exception e) {
                    responseJson = "{\"error\": \"An error occurred while processing your request\"}";
                }
            } else {
                responseJson = "{\"works\": [";
                for(int i = 0; i < works.size(); i++) {
                    responseJson += String.format("{\"work_title\": \"%s\", \"work_id\": \"%s\"}", works.get(i).getTitle(), works.get(i).getId());
                    if(i < works.size() - 1) {
                        responseJson += ",";
                    }
                }
            }
        }

        return responseJson;
    }
    
}
