package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Todo;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.UserRepository;
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
@RequestMapping("/api/users")
public class UserController {

    private final String URL = "https://gorest.co.in/public/v2/users";

    @Autowired
    private UserRepository repository;

    @GetMapping
    public ResponseEntity<Iterable<User>> getAll(){

        return new ResponseEntity<> ( repository.findAll (), HttpStatus.OK );

    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable int id){

        return new ResponseEntity<User> ( repository.findById ( id ).orElseThrow (() -> new ResponseStatusException ( HttpStatus.NOT_FOUND) ), HttpStatus.OK );

    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User newUser){

        return new ResponseEntity<User> (repository.save ( newUser ),HttpStatus.CREATED );

    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createByGoRestId(@PathVariable int id, RestTemplate restTemplate){

        String url = URL + "/" +id;

        try {

            User response = restTemplate.getForObject ( url, User.class );

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

            ArrayList<User> userArrayList = new ArrayList<>();

            ResponseEntity<User[]> response = restTemplate.getForEntity( URL,User[].class );

            userArrayList.addAll ( Arrays.asList ( Objects.requireNonNull ( response.getBody () ) ) );

            int totalPageNumber = Integer.parseInt (
                    Objects.requireNonNull (
                            response.getHeaders ().get (
                                    "X-Pagination-Pages" ) ).get ( 0 ) );

            for (int i = 2; i <= 5; i++) {

                String tempURl = URL + "?page=" + i;

                User[] pageData = restTemplate.getForObject ( tempURl,User[].class );

                assert pageData != null;

                userArrayList.addAll ( Arrays.asList ( pageData));

            }

            for (User user:
                    userArrayList) {

                repository.save ( user );
            }

            System.out.println (userArrayList.size ());

            return new ResponseEntity ( userArrayList,HttpStatus.OK );

        } catch( Exception e ){

            return ApiErrorHandling.genericApiError ( e );

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity updateById(@PathVariable int id, @RequestBody User updatedUser){

        User user = repository.findById ( id ).orElseThrow (() ->new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        if (updatedUser.getName () != null){
            user.setName ( updatedUser.getName () );
        }
        if (updatedUser.getEmail () != null){
            user.setEmail ( updatedUser.getEmail () );
        }
        if (updatedUser.getStatus () != null){
            user.setStatus ( updatedUser.getStatus () );
        }
        if (updatedUser.getGender () != null){
            user.setGender ( updatedUser.getGender () );
        }

        return new ResponseEntity (repository.save ( user ), HttpStatus.OK );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable int id){

        User user = repository.findById ( id ).orElseThrow (() ->new ResponseStatusException ( HttpStatus.NOT_FOUND ));

        repository.deleteById ( id );

        return new ResponseEntity ( "Id: "+ user.getId ()+ " has been deleted", HttpStatus.OK );
    }

    @DeleteMapping("/empty")
    public ResponseEntity deleteAll(){

        int repoSize = (int)repository.count ();

        repository.deleteAll ();

        return new ResponseEntity ( repoSize + " User(s) have been deleted", HttpStatus.OK );
    }
}
