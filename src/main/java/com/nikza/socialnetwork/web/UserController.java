package com.nikza.socialnetwork.web;

import com.nikza.socialnetwork.dto.CommunityDTO;
import com.nikza.socialnetwork.dto.UserDTO;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.facade.CommunityFacade;
import com.nikza.socialnetwork.facade.UserFacade;
import com.nikza.socialnetwork.service.UserService;
import com.nikza.socialnetwork.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private CommunityFacade communityFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @GetMapping
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        UserDTO userDTO = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        UserDTO userDTO = userFacade.userToUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/communities")
    public ResponseEntity<List<CommunityDTO>> getFollowedCommunities(Principal principal) {
        List<CommunityDTO> communityDTOList = userService.getCommunitiesFollowedByUser(principal)
                .stream()
                .map(communityFacade::communityToCommunityDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(communityDTOList, HttpStatus.OK);
    }

    @GetMapping("/communities/createdCommunities")
    public ResponseEntity<List<CommunityDTO>> getCreatedCommunities(Principal principal) {
        List<CommunityDTO> communityDTOList = userService.getCommunitiesCreatedByUser(principal)
                .stream()
                .map(communityFacade::communityToCommunityDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(communityDTOList, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {

        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) return errors;

        User user = userService.updateUser(userDTO, principal);
        UserDTO userUpdated = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }
}
