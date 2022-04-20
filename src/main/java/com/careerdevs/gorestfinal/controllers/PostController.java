package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final String URL = "https://gorest.co.in/public/v2/posts";

    @Autowired
    private PostRepository repository;

    @GetMapping
    public ResponseEntity<Iterable<Post>> getAllPosts(){

        return new ResponseEntity<> ( repository.findAll (), HttpStatus.OK );

    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id){

        return new ResponseEntity<> ( repository.findById ( id ).orElseThrow (() -> new ResponseStatusException ( HttpStatus.NOT_FOUND) ), HttpStatus.OK );

    }


    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post newPost){

        return new ResponseEntity<Post> (repository.save ( newPost ),HttpStatus.CREATED );

    }

    //O> Need Help with setting this up
    //could not execute statement; SQL [n/a]; nested exception is org.hibernate.exception.DataException: could not execute statement
    @PostMapping("/{id}")
    public ResponseEntity<Post> createPostByGoRestId(@PathVariable int id, RestTemplate restTemplate){

        String postUrl = URL + "/" +id;

        try {
;


//            repository.save ( request );
//
//            return new ResponseEntity<> ( request, HttpStatus.OK );

            return  new ResponseEntity<Post> (repository.save ( Objects.requireNonNull ( restTemplate.getForObject ( postUrl, Post.class ) ) ),HttpStatus.CREATED );

        }catch (Exception e){

            System.out.println (e.getClass ());

            System.out.println (e.getMessage ());

            return new ResponseEntity( e.getMessage (), HttpStatus.INTERNAL_SERVER_ERROR );
        }

    }


    @PostMapping("/fill")
    public ResponseEntity fillDatabaseWithGoRestData(RestTemplate restTemplate){

        return null;
    }

    //@PutMapping("/{id}")

    //@DeleteMapping("/{id}")
    //@DeleteMapping("/empty")




}
