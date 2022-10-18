package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.dto.CommunityDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.ImageModel;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.exceptions.CommunityNotFoundException;
import com.nikza.socialnetwork.repository.CommunityRepository;
import com.nikza.socialnetwork.repository.ImageRepository;
import com.nikza.socialnetwork.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserService userService;

    @Autowired
    public CommunityService(CommunityRepository communityRepository, ImageRepository imageRepository, UserRepository userRepository, UserService userService) {
        this.communityRepository = communityRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<Community> getAllCommunities() {
        return communityRepository.findAllByOrderByName();
    }

    public Community createCommunity(CommunityDTO communityDTO, Principal principal) {
        User user = userService.getUserByPrincipal(principal);

        LOG.info("creating Community for User: {}", user.getEmail());

        Community community = Community.builder()
                .creator(user)
                .name(communityDTO.getName())
                .description(communityDTO.getDescription())
                .build();

        return communityRepository.save(community);
    }

    public Community getCommunityById(Long communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found: " + communityId));
    }

    public Community updateCommunity(CommunityDTO communityDTO) {
        Community community = getCommunityById(communityDTO.getId());
        community.setId(communityDTO.getId());
        community.setName(communityDTO.getName());
        community.setDescription(communityDTO.getDescription());

        LOG.info("updating Community {}", community.getId());
        return communityRepository.save(community);
    }

    public void delete(Long communityId, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Community community = getCommunityById(communityId);
        if (Objects.equals(user.getId(), community.getCreator().getId())) {
            Optional<ImageModel> imageModel = imageRepository.findByCommunityId(communityId);
            communityRepository.delete(community);
            imageModel.ifPresent(imageRepository::delete);

            LOG.info("Community {} successfully deleted", communityId);
        }
    }

    public void followCommunity(Long communityId, Principal principal){
        User user = userService.getUserByPrincipal(principal);
        Community community = getCommunityById(communityId);

        if (user.getCommunities().contains(community)) {
            user.getCommunities().remove(community);
        } else {
            user.getCommunities().add(community);
        }
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
}
