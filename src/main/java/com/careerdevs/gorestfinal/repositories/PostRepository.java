package com.careerdevs.gorestfinal.repositories;

import com.careerdevs.gorestfinal.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post,Integer> {
}
