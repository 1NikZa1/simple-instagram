package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.dto.CommunityDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.ImageModel;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.repository.CommunityRepository;
import com.nikza.socialnetwork.repository.ImageRepository;
import com.nikza.socialnetwork.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommunityService {

    public static final Logger LOG = LoggerFactory.getLogger(CommunityService.class);

    private final CommunityRepository communityRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository, ImageRepository imageRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public List<Community> getAllCommunities() {
        return communityRepository.findAllByOrderByName();
    }

    public Community createCommunity(CommunityDTO communityDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Community community = new Community();
        community.setCreator(user);
        community.setName(communityDTO.getName());
        community.setDescription(communityDTO.getDescription());

        LOG.info("creating Community for User: {}", user.getEmail());
        return communityRepository.save(community);
    }

    public Community getCommunityById(Long communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(RuntimeException::new);
    }

    public Community updateCommunity(CommunityDTO communityDTO) {
        Community community = getCommunityById(communityDTO.getId());
        community.setId(communityDTO.getId());
        community.setName(communityDTO.getName());
        community.setDescription(communityDTO.getDescription());
        return communityRepository.save(community);
    }

    public void delete(Long communityId, Principal principal) {
        User user = getUserByPrincipal(principal);
        Community community = getCommunityById(communityId);
        if (Objects.equals(user.getId(), community.getCreator().getId())) {
            Optional<ImageModel> imageModel = imageRepository.findByCommunityId(communityId);
            communityRepository.delete(community);
            imageModel.ifPresent(imageRepository::delete);
        }
    }

    public void followCommunity(Long communityId, Principal principal){
        User user = getUserByPrincipal(principal);
        Community community = getCommunityById(communityId);
        community.addUserToCommunity(user);
        user.addCommunityToUser(community);
        System.out.println(community.getUsers().size());
        userRepository.save(user);
        LOG.info("followed Community: {}", community.getId());
    }

    public User getAdmin(Long communityId){
        Community community = getCommunityById(communityId);
        return community.getCreator();
    }

    public List<User> getFollowedUsers(Long communityId){
        return userRepository.findAllByCommunities_id(communityId);
    }

    public User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }


}
