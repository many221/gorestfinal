package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
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
    public ResponseEntity<Post> getPostById(@PathVariable int id){

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
            Post response = restTemplate.getForObject ( postUrl, Post.class );
            ;
            System.out.println (response);

            assert response != null;

            return  new ResponseEntity<Post> (repository.save ( response ),HttpStatus.CREATED );

        }catch (Exception e){

            System.out.println (e.getClass ());

            System.out.println (e.getMessage ());

            return new ResponseEntity( e.getMessage (), HttpStatus.INTERNAL_SERVER_ERROR );
        }

    }


    @PostMapping("/fill")
    public ResponseEntity fillDatabaseWithGoRestData(RestTemplate restTemplate){

        try {

            ArrayList<Post> postArrayList = new ArrayList<>();

            ResponseEntity<Post[]> response = restTemplate.getForEntity( URL,Post[].class );

            postArrayList.addAll ( Arrays.asList ( Objects.requireNonNull ( response.getBody () ) ) );

            int totalPageNumber = Integer.parseInt (
                    Objects.requireNonNull (
                            response.getHeaders ().get (
                                    "X-Pagination-Pages" ) ).get ( 0 ) );

            for (int i = 2; i <= 5; i++) {

                String tempURl = URL + "?page=" + i;

                Post[] pageData = restTemplate.getForObject ( tempURl,Post[].class );

                assert pageData != null;

                postArrayList.addAll ( Arrays.asList ( pageData));

            }

            for (Post post:
                    postArrayList) {

                repository.save ( post );
            }

            System.out.println (postArrayList.size ());

            return new ResponseEntity ( postArrayList,HttpStatus.OK );

        } catch( Exception e ){

            System.out.println (e.getClass ());
            System.out.println (e.getMessage ());

            return new ResponseEntity( e.getMessage (), HttpStatus.INTERNAL_SERVER_ERROR );

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity updateUserById(@PathVariable int id, @RequestBody Post updatedPost){

        Post oldPost = repository.findById ( id ).orElseThrow (() ->new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        if (updatedPost.getTitle () != null){
            oldPost.setTitle ( updatedPost.getTitle () );
        }
        if (updatedPost.getBody () != null){
            oldPost.setBody ( updatedPost.getBody () );
        }

        return new ResponseEntity (repository.save ( oldPost ), HttpStatus.OK );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable int id){

        Post post = repository.findById ( id ).orElseThrow (() -> new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        repository.deleteById ( id );

        return new ResponseEntity ( "Id: "+ post.getId ()+", Title: " +post.getTitle () + " has been deleted", HttpStatus.OK );
    }

    @DeleteMapping("/empty")
    public ResponseEntity deleteAllUsers(){

        int repoSize = (int)repository.count ();

        repository.deleteAll ();

        return new ResponseEntity ( repoSize + " post(s) have been deleted", HttpStatus.OK );
    }


}
