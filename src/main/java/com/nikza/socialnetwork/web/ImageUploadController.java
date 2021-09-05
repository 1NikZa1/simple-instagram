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

        return new ResponseEntity<>(new MessageResponse("Image upload successfully"), HttpStatus.OK);
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException{
        imageUploadService.uploadImageToPost(file, principal, Long.parseLong(postId));

        return new ResponseEntity<>(new MessageResponse("Image upload successfully"), HttpStatus.OK);
    }

    @PostMapping("community/{communityId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToCommunity(@PathVariable("communityId") String communityId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException{
        imageUploadService.uploadImageToCommunity(file, principal, Long.parseLong(communityId));

        return new ResponseEntity<>(new MessageResponse("Image upload successfully"), HttpStatus.OK);
    }

    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageToUser(Principal principal){
        ImageModel userImage = imageUploadService.getImageToUser(principal);

        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/community/{communityId}/image")
    public ResponseEntity<ImageModel> getImageToCommunity(@PathVariable("communityId") String communityId){
        ImageModel communityImage = imageUploadService.getImageToCommunity(Long.parseLong(communityId));

        return new ResponseEntity<>(communityImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getImageToPost(@PathVariable("postId") String postId){
        ImageModel postImage = imageUploadService.getImageToPost(Long.parseLong(postId));

        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }
}
