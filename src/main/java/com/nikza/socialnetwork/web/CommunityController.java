package com.nikza.socialnetwork.web;

import com.nikza.socialnetwork.dto.CommunityDTO;
import com.nikza.socialnetwork.dto.UserDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.facade.CommunityFacade;
import com.nikza.socialnetwork.facade.UserFacade;
import com.nikza.socialnetwork.payload.response.MessageResponse;
import com.nikza.socialnetwork.service.CommunityService;
import com.nikza.socialnetwork.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/community")
@CrossOrigin
public class CommunityController {

    @Autowired
    private CommunityService communityService;
    @Autowired
    private CommunityFacade communityFacade;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @GetMapping("/all")
    public ResponseEntity<List<CommunityDTO>> getAllCommunities() {
        List<CommunityDTO> communityDTOList = communityService.getAllCommunities()
                .stream()
                .map(communityFacade::communityToCommunityDTO)
                .toList();
        return new ResponseEntity<>(communityDTOList, HttpStatus.OK);
    }

    @GetMapping("/{communityId}/followed")
    public ResponseEntity<List<UserDTO>> getFollowedUsers(@PathVariable("communityId") Long communityId) {
        List<UserDTO> followedUsers = communityService.getFollowedUsers(communityId)
                .stream()
                .map(userFacade::userToUserDTO)
                .toList();
        return new ResponseEntity<>(followedUsers, HttpStatus.OK);
    }

    @GetMapping("/{communityId}/follow")
    public ResponseEntity<MessageResponse> followCommunity(@PathVariable("communityId") Long communityId,
                                                              Principal principal) {
        communityService.followCommunity(communityId, principal);
        return new ResponseEntity<>(new MessageResponse("subscribed successfully"), HttpStatus.OK);
    }

    @GetMapping("/{communityId}/admin")
    public ResponseEntity<UserDTO> getAdmin(@PathVariable("communityId") Long communityId) {
        User user = communityService.getAdmin(communityId);
        return new ResponseEntity<>(userFacade.userToUserDTO(user), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createCommunity(@Valid @RequestBody CommunityDTO communityDTO,
                                                  BindingResult bindingResult,
                                                  Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) return errors;
        Community community = communityService.createCommunity(communityDTO, principal);
        CommunityDTO createdCommunity = communityFacade.communityToCommunityDTO(community);

        return new ResponseEntity<>(createdCommunity, HttpStatus.OK);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityDTO> getCommunityProfile(@PathVariable("communityId") Long communityId) {
        Community community = communityService.getCommunityById(communityId);
        CommunityDTO communityDTO = communityFacade.communityToCommunityDTO(community);

        return new ResponseEntity<>(communityDTO, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/update")
    public ResponseEntity<Object> updateCommunity(@Valid @RequestBody CommunityDTO communityDTO,
                                                  BindingResult bindingResult,
                                                  Principal principal,
                                                  @PathVariable("communityId") Long communityId) {

        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) return errors;

        communityDTO.setId(communityId);
        Community community = communityService.updateCommunity(communityDTO, principal);

        return new ResponseEntity<>(communityFacade.communityToCommunityDTO(community), HttpStatus.OK);
    }

    @PostMapping("/{communityId}/delete")
    public ResponseEntity<MessageResponse> deleteCommunity(@PathVariable("communityId") Long communityId,
                                                      Principal principal) {
        communityService.delete(communityId, principal);

        return new ResponseEntity<>(new MessageResponse("Community was deleted"), HttpStatus.OK);
    }
}
