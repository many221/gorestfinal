package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.Todo;
import com.careerdevs.gorestfinal.repositories.TodoRepository;
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
@RequestMapping("/api/todos")
public class TodoController {

    private final String URL = "https://gorest.co.in/public/v2/todos";

    @Autowired
    private TodoRepository repository;

    @GetMapping
    public ResponseEntity<Iterable<Todo>> getAllTodos(){

        return new ResponseEntity<> ( repository.findAll (), HttpStatus.OK );

    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable int id){

        return new ResponseEntity<> ( repository.findById ( id ).orElseThrow (() -> new ResponseStatusException ( HttpStatus.NOT_FOUND) ), HttpStatus.OK );

    }

    @PostMapping
    public ResponseEntity<Todo> createPost(@RequestBody Todo newTodo){

        return new ResponseEntity<Todo> (repository.save ( newTodo ),HttpStatus.CREATED );

    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createPostByGoRestId(@PathVariable int id, RestTemplate restTemplate){

        String url = URL + "/" +id;

        try {

            Todo response = restTemplate.getForObject ( url, Todo.class );

            System.out.println (response);

            assert response != null;

            return  new ResponseEntity (repository.save ( response ),HttpStatus.CREATED );

        }catch (Exception e){

            return  ApiErrorHandling.genericApiError ( e );
        }

    }

    @PostMapping("/fill")
    public ResponseEntity fillDatabaseWithGoRestData(RestTemplate restTemplate){

        try {

            ArrayList<Todo> todoArrayList = new ArrayList<>();

            ResponseEntity<Todo[]> response = restTemplate.getForEntity( URL,Todo[].class );

            todoArrayList.addAll ( Arrays.asList ( Objects.requireNonNull ( response.getBody () ) ) );

            int totalPageNumber = Integer.parseInt (
                    Objects.requireNonNull (
                            response.getHeaders ().get (
                                    "X-Pagination-Pages" ) ).get ( 0 ) );

            for (int i = 2; i <= 5; i++) {

                String tempURl = URL + "?page=" + i;

                Todo[] pageData = restTemplate.getForObject ( tempURl,Todo[].class );

                assert pageData != null;

                todoArrayList.addAll ( Arrays.asList ( pageData));

            }

            for (Todo todo:
                    todoArrayList) {

                repository.save ( todo );
            }

            System.out.println (todoArrayList.size ());

            return new ResponseEntity ( todoArrayList,HttpStatus.OK );

        } catch( Exception e ){

            return ApiErrorHandling.genericApiError ( e );

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity updateoById(@PathVariable int id, @RequestBody Todo updatedTodo){

        Todo todo = repository.findById ( id ).orElseThrow (() ->new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        if (updatedTodo.getTitle () != null){
            todo.setTitle ( updatedTodo.getTitle () );
        }
        if (updatedTodo.getDue_on () != null){
            todo.setDue_on ( updatedTodo.getDue_on () );
        }
        if (updatedTodo.getStatus () != null){
            todo.setStatus ( updatedTodo.getStatus () );
        }

        return new ResponseEntity (repository.save ( todo ), HttpStatus.OK );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable int id){

        Todo todo = repository.findById ( id ).orElseThrow (() -> new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        repository.deleteById ( id );

        return new ResponseEntity ( "Id: "+ todo.getId ()+ " has been deleted", HttpStatus.OK );
    }

    @DeleteMapping("/empty")
    public ResponseEntity deleteAll(){

        int repoSize = (int)repository.count ();

        repository.deleteAll ();

        return new ResponseEntity ( repoSize + " Todo(s) have been deleted", HttpStatus.OK );
    }

}
