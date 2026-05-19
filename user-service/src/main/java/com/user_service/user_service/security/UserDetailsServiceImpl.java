package com.user_service.user_service.security;


import com.user_service.user_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.user_service.user_service.entity.User;



@Service
//@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not Found"));


        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();

    }

}


//we return it in this format coz spring security expects return type of UserDetails

//Spring Security needs:
//username password
//roles/authorities
//to perform authentication.

//So we convert our DB User entity into Spring Security’s UserDetails object.

//We return Spring Security’s User object because authentication
//internally works with the UserDetails interface, not our custom entity

// we are doing this for this login flow
//POST /login
//     ↓
//AuthenticationManager
//     ↓
//UserDetailsServiceImpl
//     ↓
//findByEmail()
//     ↓
//PasswordEncoder matches password
//     ↓
//JWT generated


//UserDetailsService itself IS a service contract provided by Spring Security. so during authentication
//Spring Security automatically looks for UserDetailsService

//
//
//Spring Security internally calls:
//
//loadUserByUsername()
//
//automatically during login.
//
//So by implementing the interface:
//
//less custom code
//integrates directly with Spring Security