package com.spaceyatech.berlin.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spaceyatech.berlin.enums.RoleName;
import com.spaceyatech.berlin.models.Role;
import com.spaceyatech.berlin.models.User;
import com.spaceyatech.berlin.repository.RoleRepository;
import com.spaceyatech.berlin.repository.UserRepository;
import com.spaceyatech.berlin.requests.LoginRequest;
import com.spaceyatech.berlin.requests.SignUpRequest;
import com.spaceyatech.berlin.requests.TokenRefreshRequest;
import com.spaceyatech.berlin.response.AllUsersResponse;
import com.spaceyatech.berlin.response.JwtResponse;
import com.spaceyatech.berlin.response.MessageResponse;
import com.spaceyatech.berlin.response.TokenRefreshResponse;
import com.spaceyatech.berlin.security.jwt.JwtUtils;
import com.spaceyatech.berlin.utilities.Dry;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import liquibase.pro.packaged.A;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class UserService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse signIn(LoginRequest loginRequest){


        log.info("sign in request:-->{}",loginRequest.getUsername());


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );


        log.info("authentication data:-->{}", authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);//This is where we store details of the present security context of the application, which includes details of the principal currently using the application.
        String access_token = jwtUtils.generateJwtToken(authentication);
        String refresh_token = jwtUtils.generateJwtRefreshToken(authentication);


        UserDetailss userDetails = (UserDetailss) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());



        JwtResponse jwtResponse = JwtResponse.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .roles(roles)
                .access_token(access_token)
                .refresh_token(refresh_token)
                .type("Bearer")
                .username(userDetails.getUsername())
                .build();

        log.info("jwtResponse:-->{}", jwtResponse);

        return jwtResponse;

    }

    public MessageResponse signUp(SignUpRequest signUpRequest){

        MessageResponse msg;


        try{
            log.info("signup request:-->{}",signUpRequest);

            if(userRepository.existsByUsername(signUpRequest.getUsername())) {
                return new MessageResponse("Error: Username is already taken!");

            }

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                return new MessageResponse("Error: Email is already in use!");

            }

            // Create new user's account
            String date = Dry.getCurrentDate();

            log.info("getcurrentdate:-->{}",date);

            User user = User.builder()
                    .username(signUpRequest.getUsername())
                    .email(signUpRequest.getEmail())
                    .password(encoder.encode(signUpRequest.getPassword()))
                    .date_created(Timestamp.valueOf(date))//change to proper date
                    .build();



            log.info("user being saved:-->{}",user.toString());


            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);

                            break;
                        case "mod":
                            Role modRole = roleRepository.findByName(RoleName.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(modRole);

                            break;
                        default:
                            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                    }
                });
            }

            user.setRole(roles);


            try{
                userRepository.save(user);
                //TODO: we can send email alert
                 msg = MessageResponse.builder()
                        .message("User registered successfully!")
                        .build();
                log.info("Registered:-->{}",msg);
            }catch (Exception e){
                log.error("Not Registered:-->{}",e.getMessage());
                msg = MessageResponse.builder()
                        .message("User failed to register!"+e.getMessage())
                        .build();
                log.error("NOT Registered:-->{}",msg);
            }



        }catch (Exception e){
            log.error("Error saving new user:-->{}",e.getMessage());
            msg = MessageResponse.builder()
                    .message("User failed to register!"+e.getMessage())
                    .build();
        }

        return msg;

    }

    public TokenRefreshResponse genarateRefreshToken(TokenRefreshRequest tokenRefreshRequest) {
        log.info("old refresh-token requestBody():-->{}",tokenRefreshRequest);

          String refresh_token = tokenRefreshRequest.getRefreshToken();

        //logic to create new refresh/access token
        String newrefreshtoken="";
        String newaccesstoken="";

        if (refresh_token != null && jwtUtils.validateJwtToken(refresh_token)) {



            String username = jwtUtils.getUserNameFromJwtToken(refresh_token);
            log.info("username from refresh token:-->{}",username);

            DecodedJWT jwt = JWT.decode(refresh_token);
            if( jwt.getExpiresAt().before(new Date())) {

                log.info("-->refresh token is expired");
                newrefreshtoken = jwtUtils.newRefreshToken(username);
            }else{
                log.info("-->refresh token not expired");
                newrefreshtoken = refresh_token;
            }
           // newrefreshtoken = jwtUtils.newRefreshToken(username);
            newaccesstoken  = jwtUtils.newAccessToken(username);

            log.info("newrefreshtoken:-->{} ,newaccesstoken:-->{}",newrefreshtoken,newaccesstoken);


        }



        TokenRefreshResponse tokenRefreshResponse = TokenRefreshResponse.builder()
                .refreshToken(newrefreshtoken)
                .accessToken(newaccesstoken)
                .tokenType("Bearer")
                .build();


        log.info("new refresh-token/access-token responseBody():-->{}",tokenRefreshResponse);
        return tokenRefreshResponse;
    }

    public List<AllUsersResponse> allUsers(){

        List<User> userList =  userRepository.findAll();

        log.info("we have {} users ",userList.size()); //userList.size();


        List<AllUsersResponse> allusers = new ArrayList<AllUsersResponse>();
        for(User user : userList){

            AllUsersResponse allUsersResponse = new AllUsersResponse();
            allUsersResponse.setId(user.getId());
            allUsersResponse.setEmail(user.getEmail());
            allUsersResponse.setUsername(user.getUsername());
            allUsersResponse.setPhone_number(user.getPhone_number());
            allUsersResponse.setDate_created(""+user.getDate_created());

            List<Role> roles=new ArrayList();

            roles.addAll(user.getRole().stream().toList());

            allUsersResponse.setRole(roles);

            allusers.add(allUsersResponse);
        }
       // log.info("all users response:---------->{}",allusers);


        return allusers;

    }

    public void saveRole(Role role){

       //check if roles are present in db before we save them

        Optional<Role> adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN);
        Optional<Role> userRole = roleRepository.findByName(RoleName.ROLE_USER);
        Optional<Role> modRole = roleRepository.findByName(RoleName.ROLE_MODERATOR);

        if(adminRole.isPresent() && userRole.isPresent() && modRole.isPresent()){
            log.info("adminRole.isPresent():{} ,userRole.isPresent():{},modRole.isPresent() :{}",adminRole.isPresent(),userRole.isPresent() , modRole.isPresent());
        }else{
            log.info("we are saving this role on start up :{}",role);
             roleRepository.save(role);
        }


        //return roleRepository.save(role);
    }
    public AllUsersResponse  findUserById(UUID id){

       Optional<User>  userById =userRepository.findById(id);

       AllUsersResponse response;
       if(userById.isPresent()){
           User user=userById.get();
           response=new AllUsersResponse();
           response.setId(user.getId());
           response.setEmail(user.getEmail());
           response.setUsername(user.getUsername());
           response.setDate_created(""+user.getDate_created());
           response.setRole(user.getRole().stream().toList());
       }else{
           log.info("user with id : {} not found ",id);
           throw new RuntimeException("user with id " +id+ " not found");

       }

        log.info("user object response.. user id :{} username : {} user email :{} ",response.getId(),response.getUsername(),response.getEmail());

       return response;
    }
}
