package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.dto.UserDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.Role;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.exceptions.UserExistException;
import com.nikza.socialnetwork.payload.request.SignupRequest;
import com.nikza.socialnetwork.repository.CommunityRepository;
import com.nikza.socialnetwork.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, CommunityRepository communityRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(SignupRequest userIn) throws UserExistException {
        User user = new User();

        user.setName(userIn.getFirstname());
        user.setLastname(userIn.getLastname());
        user.setUsername(userIn.getUsername());
        user.setEmail(userIn.getEmail());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRole().add(Role.ROLE_USER);
        try {
            LOG.info("saving user {}", userIn.getEmail());
            userRepository.save(user);
        } catch (Exception ex) {
            LOG.error("error of reg.{}", ex.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist");
        }
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setName(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setUsername(userDTO.getUsername());
        user.setBio(userDTO.getBio());
        return userRepository.save(user);
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    public User getUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public List<Community> getCommunitiesFollowedByUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        return communityRepository.findAllByUsers_id(user.getId());
    }

    public List<Community> getCommunitiesCreatedByUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        return communityRepository.findAllByCreator_id(user.getId());
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }
}
