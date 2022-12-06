package com.spaceyatech.berlin.services;

import com.spaceyatech.berlin.interfaces.AccountInterface;
import com.spaceyatech.berlin.models.Account;
import com.spaceyatech.berlin.models.User;
import com.spaceyatech.berlin.repository.AccountRepository;
import com.spaceyatech.berlin.repository.UserRepository;
import com.spaceyatech.berlin.requests.AccountRequest;
import com.spaceyatech.berlin.response.AccountResponse;
import com.spaceyatech.berlin.response.MessageResponse;
import com.spaceyatech.berlin.utilities.Dry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class AccountService implements AccountInterface {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {
        log.info("create account request:{} ",accountRequest);

        String name = accountRequest.getName();
        UUID userId = accountRequest.getUserId();
        Account account;
        Account createdAccount;
        if( name != null && !name.isEmpty() && userId !=null){
            //get user
            Optional<User> optionalUser = userRepository.findById(userId);

            if(optionalUser.isPresent()){
                //date
                String date = Dry.getCurrentDate();

                //account builder
                account = Account.builder()
                        .name(accountRequest.getName())
                        .bio_data(accountRequest.getBioData())
                        .display_photo(accountRequest.getDisplayPhoto())
                        .user(optionalUser.get())
                        .CreatedAT(Timestamp.valueOf(date))
                        .UpdatedAT(Timestamp.valueOf(date))
                        .build();

                try{
                    //persist account details to db
                    createdAccount =accountRepository.save(account);
                }catch (Exception e){
                    log.info("failed to save account :{}",e.getMessage());
                    throw new RuntimeException("failed to save user account : "+e.toString());
                }

            }else{
                log.error("user with id:{} not present",accountRequest.getUserId());
                throw new RuntimeException("user with id : "+accountRequest.getUserId()+ "not present");
            }
        }else{
            log.warn("userId and name cannot be blank");
            throw new RuntimeException("userId and name cannot be blank");
        }


        //build response
        AccountResponse accountResponse = AccountResponse.builder()
                .name(createdAccount.getName())
                .bioData(createdAccount.getBio_data())
                .CreatedAT(createdAccount.getCreatedAT())
                .UpdatedAT(createdAccount.getUpdatedAT())
                .userId(createdAccount.getUser().getId())
                .accountId(createdAccount.getId())
                .build();
        log.info("Account Created Response: {}",accountResponse);

        return accountResponse;


    }

    @Override
    public AccountResponse updateAccount(AccountRequest accountRequest,UUID accountId) {
       Account optionalAccount = accountRepository.findById(accountId).get();
       Account updatedAccount;

       if(optionalAccount.getId()== accountId){
           //date
           String date = Dry.getCurrentDate();
           if(accountRequest.getName() != null && !accountRequest.getName().isEmpty() && accountRequest.getBioData() !=null){
           optionalAccount.setName(accountRequest.getName().trim());
           optionalAccount.setBio_data(accountRequest.getBioData());
           optionalAccount.setUpdatedAT(Timestamp.valueOf(date));
           optionalAccount.setDisplay_photo(accountRequest.getDisplayPhoto());

           try{
               //just to make sure update was done
              updatedAccount = accountRepository.save(optionalAccount);

           }catch (Exception e){
               log.error("failed to update account :{}",e.getMessage());
               throw new RuntimeException("failed to update account account : "+e.toString());
           }
           }else{
               log.warn("accountname or biodata cannot be blank");
               throw new RuntimeException("accountname or biodata cannot be blank");
           }

       }else{
           log.error("account with id:{} not present",accountId);
           throw new RuntimeException("account with id : "+accountId+ "not present");
       }
        //build response
        AccountResponse response = AccountResponse.builder()

                .name(updatedAccount.getName())
                .bioData(updatedAccount.getBio_data())
                .displayPhoto(updatedAccount.getDisplay_photo())
                .CreatedAT(updatedAccount.getCreatedAT())
                .UpdatedAT(updatedAccount.getUpdatedAT())
                .userId(updatedAccount.getUser().getId())
                .accountId(updatedAccount.getId())

                .build();
        log.info("Account updated Response: {}",response);


        return response;
    }

    @Override
    public List<AccountResponse> fetchAllAccountsAssociatedToUserByUserId(UUID userId) {

        List<Account> optionalAccount = accountRepository.findByUserId(userId);
        List<AccountResponse> response = new ArrayList<AccountResponse>();
        if(!optionalAccount.isEmpty()){

             //builder pattern with a list
            for(Account account : optionalAccount){

                AccountResponse accountResponse = AccountResponse.builder()
                        .name(account.getName())
                        .accountId(account.getId())
                        .userId(account.getUser().getId())
                        .bioData(account.getBio_data())
                        .UpdatedAT(account.getUpdatedAT())
                        .CreatedAT(account.getCreatedAT())
                        .displayPhoto(account.getDisplay_photo())
                .build();


                response.add(accountResponse);
            }


        }else {
            log.error("no account associated with userId:  {} supplied ",userId);
            throw new RuntimeException("no account associated with userId:"+userId+"supplied ");
        }
       log.info("all user accounts :{}",response);

       return response;
    }

    @Override
    public AccountResponse fetchAccountByAccountId(UUID accountId) {

       Optional<Account> account =   accountRepository.findById(accountId);



       AccountResponse response ;
       if(account.isPresent()) {
           response = AccountResponse.builder()
                   .name(account.get().getName())
                   .userId(account.get().getUser().getId())
                   .bioData(account.get().getBio_data())
                   .displayPhoto(account.get().getDisplay_photo())
                   .CreatedAT(account.get().getCreatedAT())
                   .UpdatedAT(account.get().getUpdatedAT())
                   .accountId(account.get().getId())
                   .build();
       }else{
           log.error("account with id:{} not present",accountId);
           throw new RuntimeException("account with id : "+accountId+ "not present");
       }

        log.info("Account found by Id Response: {}",response);
       return response;
    }

    @Override
    public MessageResponse deleteAccountByAccountId(UUID accountId) {
        MessageResponse response;
        try{
            accountRepository.deleteById(accountId);
            response = MessageResponse.builder()
                    .message("account : "+accountId+" deleted successfully")
                    .build();
        }catch (Exception e){
            log.error("failed to delete account :{} ,error :{}",accountId,e.getMessage());
            throw new RuntimeException("failed to delete account :"+e.getMessage());
        }


        //https://medium.com/javarevisited/springboot-app-monitoring-with-grafana-prometheus-7c723f0dec15

        return response ;

    }

}
