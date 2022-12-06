package com.spaceyatech.berlin.interfaces;

import com.spaceyatech.berlin.requests.AccountRequest;
import com.spaceyatech.berlin.response.AccountResponse;
import com.spaceyatech.berlin.response.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface AccountInterface {

    public AccountResponse createAccount(AccountRequest accountRequest);
    public AccountResponse updateAccount(AccountRequest accountRequest,UUID accountId);
    public List<AccountResponse> fetchAllAccountsAssociatedToUserByUserId(UUID userId);
    public AccountResponse fetchAccountByAccountId(UUID accountId);
    public MessageResponse deleteAccountByAccountId(UUID accountId);

}
