package com.ballsquad.ballsquad.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.ballsquad.ballsquad.model.Author;

@Repository
public interface AuthorRepo extends CrudRepository<Author, String>{
    public Author findByName(String name);
}
