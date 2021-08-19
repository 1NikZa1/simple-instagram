package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.entity.enums.Role;
import com.nikza.socialnetwork.exceptions.UserExistException;
import com.nikza.socialnetwork.payload.request.SignupRequest;
import com.nikza.socialnetwork.repository.UserRepository;
import com.nikza.socialnetwork.security.JWTAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(SignupRequest userIn) throws UserExistException {
        User user = new User();

        user.setName(userIn.getFirstname());
        user.setLastname(userIn.getLastname());
        user.setUsername(userIn.getUsername());
        user.setEmail(userIn.getEmail());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRole().add(Role.ROLE_USER);
        try{
            LOG.info("saving user {}", userIn.getEmail());
            return userRepository.save(user);
        }catch (Exception ex){
            LOG.error("error of reg.{}", ex.getMessage());
            throw new UserExistException("The user " + user.getUsername()+" already exist");
        }

    }
}
