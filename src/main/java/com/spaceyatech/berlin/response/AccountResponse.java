package com.spaceyatech.berlin.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private String displayPhoto;
    private String name;
    private Timestamp CreatedAT;
    private Timestamp UpdatedAT;
    private String bioData;
    private UUID userId;

    private UUID accountId;
}
