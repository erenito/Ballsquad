package com.ballsquad.ballsquad;

import com.ballsquad.ballsquad.controller.AppController;
import com.ballsquad.ballsquad.model.Author;
import com.ballsquad.ballsquad.model.Work;
import com.ballsquad.ballsquad.repo.AuthorRepo;
import com.ballsquad.ballsquad.repo.WorkRepo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class BallsquadApplicationTests {

    @Autowired
    private AppController appController;

    @MockBean
    private AuthorRepo authorRepo;

    @MockBean
    private WorkRepo workRepo;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAuthor_ExistingAuthorInDatabase() {
        // Arrange
        String authorName = "John Doe";
        String authorId = "123";
        Author author = new Author(authorName, authorId);
        when(authorRepo.findByName(authorName)).thenReturn(author);

        // Act
        ResponseEntity<String> response = appController.getAuthor(authorName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"author_name\": \"John Doe\", \"author_id\": \"123\"}", response.getBody());
    }

    @Test
    void testGetAuthor_AuthorNotFoundInDatabase() {
        // Arrange
        String authorName = "Jane Smith";
        when(authorRepo.findByName(authorName)).thenReturn(null);
        String apiResponse = "{\"numFound\": 1, \"docs\": [{\"name\": \"Jane Smith\", \"key\": \"OL4857829A\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<String> response = appController.getAuthor(authorName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"author_name\": \"Jane Smith\", \"author_id\": \"OL4857829A\"}", response.getBody());
        verify(authorRepo).save(any(Author.class));
    }

    @Test
    void testGetAuthor_AuthorNotFoundInDatabaseAndAPI() {
        // Arrange
        String authorName = "Unknown Author";
        when(authorRepo.findByName(authorName)).thenReturn(null);
        String apiResponse = "{\"numFound\": 0}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<String> response = appController.getAuthor(authorName);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"error\": \"Author not found\"}", response.getBody());
        verify(authorRepo, never()).save(any(Author.class));
    }

    @Test
    void testGetWorks_WorksFoundInDatabase() {
        // Arrange
        String authorId = "123";
        Author author = new Author("John Doe", authorId);
        List<Work> works = new ArrayList<>();
        works.add(new Work("Work 1", "work1", author.getName()));
        works.add(new Work("Work 2", "work2", author.getName()));
        when(authorRepo.findById(authorId)).thenReturn(Optional.of(author));
        when(workRepo.findByAuthor(author.getName())).thenReturn(works);

        // Act
        ResponseEntity<String> response = appController.getWorks(authorId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONArray worksJsonArray = new JSONArray();
        works.forEach(work -> {
            JSONObject workJson = new JSONObject();
            workJson.put("work_title", work.getTitle());
            workJson.put("work_id", work.getId());
            worksJsonArray.put(workJson);
        });
        JSONObject expectedResponseJson = new JSONObject();
        expectedResponseJson.put("works", worksJsonArray);
        assertEquals(expectedResponseJson.toString(), response.getBody());
    }

    @Test
    void testGetWorks_WorksNotFoundInDatabase() {
        // Arrange
		String authorId = "123";
		Author author = new Author("John Doe", authorId);
		when(authorRepo.findById(authorId)).thenReturn(Optional.of(author));
		when(workRepo.findByAuthor(author.getName())).thenReturn(new ArrayList<>());
		String apiResponse = "{\"entries\": [{\"title\": \"Work 1\", \"key\": \"/works/OL123W\"}, {\"title\": \"Work 2\", \"key\": \"/works/OL456W\"}]}";
		when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

		// Act
		ResponseEntity<String> response = appController.getWorks(authorId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONArray worksJsonArray = new JSONArray();
		worksJsonArray.put(new JSONObject("{\"work_title\": \"Work 1\", \"work_id\": \"/works/OL123W\"}"));
		worksJsonArray.put(new JSONObject("{\"work_title\": \"Work 2\", \"work_id\": \"/works/OL456W\"}"));
		JSONObject expectedResponseJson = new JSONObject();
		expectedResponseJson.put("works", worksJsonArray);
		assertEquals(expectedResponseJson.toString(), response.getBody());
		verify(workRepo, times(2)).save(any(Work.class));
    }
}
