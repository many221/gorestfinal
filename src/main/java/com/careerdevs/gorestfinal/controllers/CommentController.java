package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.utilites.ApiErrorHandling;
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
@RequestMapping("/api/comments")
public class CommentController {

    private final String URL = "https://gorest.co.in/public/v2/comments";

    @Autowired
    private CommentRepository repository;

    @GetMapping
    public ResponseEntity<Iterable<Comment>> getAllComments(){

        return new ResponseEntity<> ( repository.findAll (), HttpStatus.OK );

    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable int id){

        return new ResponseEntity<Comment> ( repository.findById ( id).orElseThrow (()-> new ResponseStatusException ( HttpStatus.NOT_FOUND )), HttpStatus.OK  );

    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment newComment){

        return new ResponseEntity<Comment>(repository.save ( newComment ),HttpStatus.CREATED);

    }

    @PostMapping("/{id}")
    public ResponseEntity createCommentByGoRestId(@PathVariable int id, RestTemplate restTemplate){

        String commentUrl = URL + "/" + id;

        try {

            Comment response = restTemplate.getForObject ( commentUrl,Comment.class);

            assert response != null;

            return  new ResponseEntity ( repository.save ( response ),HttpStatus.CREATED );

        } catch (Exception e){

            return ApiErrorHandling.genericApiError ( e );

        }

    }



    @PostMapping("/fill")
    public ResponseEntity fillDatabaseWithGoRestData(RestTemplate restTemplate){

        try {

            ArrayList<Comment> commentArrayList = new ArrayList<>();

            ResponseEntity<Comment[]> response = restTemplate.getForEntity( URL,Comment[].class );

            commentArrayList.addAll ( Arrays.asList ( Objects.requireNonNull ( response.getBody () ) ) );

            int totalPageNumber = Integer.parseInt (
                    Objects.requireNonNull (
                            response.getHeaders ().get (
                                    "X-Pagination-Pages" ) ).get ( 0 ) );

            for (int i = 2; i <= 5; i++) {

                String tempURl = URL + "?page=" + i;

                Comment[] pageData = restTemplate.getForObject ( tempURl,Comment[].class );

                assert pageData != null;

                commentArrayList.addAll ( Arrays.asList ( pageData));

            }

            for (Comment comment:
                    commentArrayList) {

                repository.save ( comment );
            }

            System.out.println (commentArrayList.size ());

            return new ResponseEntity ( commentArrayList,HttpStatus.OK );

        } catch( Exception e ){

            return ApiErrorHandling.genericApiError ( e );

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity updateCommentById(@PathVariable int id, @RequestBody Comment newComment){

        Comment comment = repository.findById ( id ).orElseThrow (() ->new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        if(newComment.getBody () != null){
            comment.setBody ( newComment.getBody ()  );
        }
        if(newComment.getEmail () != null){
            comment.setEmail ( newComment.getEmail ()  );
        }
        if(newComment.getName () != null){
            comment.setName ( newComment.getName ()  );
        }

        return new ResponseEntity ( repository.save ( comment ), HttpStatus.OK );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable int id){

        Comment comment = repository.findById ( id ).orElseThrow (() -> new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        repository.deleteById ( id );

        return new ResponseEntity ( "Id: "+ comment.getId ()+ " has been deleted", HttpStatus.OK );
    }

    @DeleteMapping("/empty")
    public ResponseEntity deleteAllUsers(){

        int repoSize = (int)repository.count ();

        repository.deleteAll ();

        return new ResponseEntity ( repoSize + " post(s) have been deleted", HttpStatus.OK );
    }
}
