package com.spaceyatech.berlin.requests;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class LoginRequest {
   @NonNull
   private String username;
   @NonNull
   private String password;
}
