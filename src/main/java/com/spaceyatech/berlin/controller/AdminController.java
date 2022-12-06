package com.spaceyatech.berlin.controller;

import com.spaceyatech.berlin.models.User;
import com.spaceyatech.berlin.response.AllUsersResponse;
import com.spaceyatech.berlin.services.AccountService;
import com.spaceyatech.berlin.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.pro.packaged.H;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@Tag(name="Admin Controller")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @GetMapping( "/allusers")
    @SecurityRequirement(name = "Bearer Authentication")

    public ResponseEntity< List<AllUsersResponse>> fetchAllUsers(){

        //return userService.allUsers();

        return ResponseEntity.status(HttpStatus.OK).body(userService.allUsers());


    }
    @PostMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")

    public ResponseEntity<AllUsersResponse> fetchUserById( @PathVariable UUID id){
        log.info("user Id specified: -->{}",id);

        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserById(id));
    }

    @GetMapping("/UserAccounts/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> findAccountsAssociatedToUserByUserId(@PathVariable("id") UUID userId){
        log.info("user Id specified: -->{}",userId);

        return ResponseEntity.status(HttpStatus.OK).body(accountService.fetchAllAccountsAssociatedToUserByUserId(userId));
    }
}
