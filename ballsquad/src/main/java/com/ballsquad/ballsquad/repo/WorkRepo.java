package com.ballsquad.ballsquad.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.ballsquad.ballsquad.model.Work;
import java.util.List;


@Repository
public interface WorkRepo extends CrudRepository<Work, String>{
    public List<Work> findByAuthor(String author);
}
