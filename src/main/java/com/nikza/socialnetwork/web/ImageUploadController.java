package com.nikza.socialnetwork.web;

import com.nikza.socialnetwork.entity.ImageModel;
import com.nikza.socialnetwork.payload.response.MessageResponse;
import com.nikza.socialnetwork.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("api/image")
@CrossOrigin
public class ImageUploadController {

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImageToUser(file, principal);

        return new ResponseEntity<>(new MessageResponse("Image upload successfully to user"), HttpStatus.OK);
    }

    @PostMapping("/bgUpload")
    public ResponseEntity<MessageResponse> uploadBackgroundToUser(@RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadBackgroundToUser(file, principal);

        return new ResponseEntity<>(new MessageResponse("Image upload successfully to user"), HttpStatus.OK);
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") Long postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException{
        imageUploadService.uploadImageToPost(file, principal, postId);

        return new ResponseEntity<>(new MessageResponse("Image upload successfully to post"), HttpStatus.OK);
    }

    @PostMapping("community/{communityId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToCommunity(@PathVariable("communityId") Long communityId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException{
        imageUploadService.uploadImageToCommunity(file, principal, communityId);

        return new ResponseEntity<>(new MessageResponse("Image upload successfully"), HttpStatus.OK);
    }

    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageToUser(Principal principal){
        ImageModel userImage = imageUploadService.getImageToUser(principal);

        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/bgImage")
    public ResponseEntity<ImageModel> getBackgroundImageToUser(Principal principal){
        ImageModel userImage = imageUploadService.getBackgroundImageToUser(principal);

        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

//    @GetMapping("/profileImage/{userId}")
//    public ResponseEntity<ImageModel> getImageToUserById(@PathVariable("userId") Long userId){
//        ImageModel userImage = imageUploadService.getImageToUserById(userId);
//
//        return new ResponseEntity<>(userImage, HttpStatus.OK);
//    }

    @GetMapping("/profileImage/{username}")
    public ResponseEntity<ImageModel> getImageToUserByUsername(@PathVariable("username") String username){
        ImageModel userImage = imageUploadService.getImageToUserByUsername(username);

        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/bgImage/{username}")
    public ResponseEntity<ImageModel> getBackgroundImageToUserByUsername(@PathVariable("username") String username){
        ImageModel userImage = imageUploadService.getBackgroundImageToUserByUsername(username);

        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/community/{communityId}/image")
    public ResponseEntity<ImageModel> getImageToCommunity(@PathVariable("communityId") Long communityId){
        ImageModel communityImage = imageUploadService.getImageToCommunity(communityId);

        return new ResponseEntity<>(communityImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getImageToPost(@PathVariable("postId") Long postId){
        ImageModel postImage = imageUploadService.getImageToPost(postId);

        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }
}
