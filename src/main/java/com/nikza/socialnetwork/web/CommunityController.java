package com.nikza.socialnetwork.web;

import com.nikza.socialnetwork.dto.CommunityDTO;
import com.nikza.socialnetwork.dto.PostDTO;
import com.nikza.socialnetwork.dto.UserDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.facade.CommunityFacade;
import com.nikza.socialnetwork.payload.response.MessageResponse;
import com.nikza.socialnetwork.service.CommunityService;
import com.nikza.socialnetwork.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/community")
@CrossOrigin
public class CommunityController {

    @Autowired
    private CommunityService communityService;
    @Autowired
    private CommunityFacade communityFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @GetMapping("/all")
    public ResponseEntity<List<CommunityDTO>> getAllPosts() {
        List<CommunityDTO> communityDTOList = communityService.getAllCommunities()
                .stream()
                .map(communityFacade::communityToCommunityDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(communityDTOList, HttpStatus.OK);
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
    public ResponseEntity<CommunityDTO> getCommunityProfile(@PathVariable("communityId") String communityId) {
        Community community = communityService.getCommunityById(Long.parseLong(communityId));
        CommunityDTO communityDTO = communityFacade.communityToCommunityDTO(community);

        return new ResponseEntity<>(communityDTO, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/update")
    public ResponseEntity<Object> updateCommunity(@Valid @RequestBody CommunityDTO communityDTO,
                                                  BindingResult bindingResult,
                                                  Principal principal,
                                                  @PathVariable("communityId") String communityId) {

        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) return errors;

        User user = communityService.getUserByPrincipal(principal);
        Community community = communityService.getCommunityById(Long.parseLong(communityId));

        if (user.getId().equals(community.getCreator().getId())) {
            communityDTO.setId(community.getId());
            community = communityService.updateCommunity(communityDTO);
        }

        CommunityDTO communityUpdated = communityFacade.communityToCommunityDTO(community);

        return new ResponseEntity<>(communityUpdated, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/delete")
    public ResponseEntity<MessageResponse> deleteCommunity(@PathVariable("communityId") String communityId,
                                                      Principal principal) {
        communityService.delete(Long.parseLong(communityId), principal);

        return new ResponseEntity<>(new MessageResponse("Community was deleted"), HttpStatus.OK);
    }
}
