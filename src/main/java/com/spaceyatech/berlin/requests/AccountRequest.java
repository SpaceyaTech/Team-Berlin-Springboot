package com.spaceyatech.berlin.requests;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class AccountRequest {

    private String displayPhoto;
    @NotNull
    private String name;

    private String bioData;
    @NotNull
    private UUID userId;
}
