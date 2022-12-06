package com.spaceyatech.berlin.controller;

import com.spaceyatech.berlin.requests.AccountRequest;
import com.spaceyatech.berlin.response.AccountResponse;
import com.spaceyatech.berlin.response.MessageResponse;
import com.spaceyatech.berlin.services.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/Account")
@Tag(name="Account Controller")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/createAccount")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest){

        log.info("create account request: {}", accountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountRequest));
    }

    @PutMapping("/updateAccount/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AccountResponse> updateAccount(@RequestBody @Valid AccountRequest accountRequest ,@PathVariable("id") UUID accountId){

        log.info("update account request: {} accountId:{}", accountRequest,accountId);
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccount(accountRequest,accountId));
    }

    @GetMapping("/findAccount/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> findAccountByAccountId(@PathVariable("id") UUID accountId){

        log.info("account Id specified: -->{}",accountId);
        return ResponseEntity.status(HttpStatus.OK).body(accountService.fetchAccountByAccountId(accountId));
    }



    @DeleteMapping("/deleteAccount/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MessageResponse> deleteAccountByAccountId(@PathVariable("id") UUID accountId){

        log.info("account Id specified: -->{}",accountId);
        return ResponseEntity.status(HttpStatus.OK).body(accountService.deleteAccountByAccountId(accountId));
    }

}
