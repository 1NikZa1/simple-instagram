package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.ImageModel;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.exceptions.ImageNotFoundException;
import com.nikza.socialnetwork.repository.CommunityRepository;
import com.nikza.socialnetwork.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageUploadService {

    public static final Logger LOG = LoggerFactory.getLogger(ImageUploadService.class);

    private final ImageRepository imageRepository;
    private final CommunityRepository communityRepository;
    private final UserService userService;


    @Autowired
    public ImageUploadService(ImageRepository imageRepository, CommunityRepository communityRepository, UserService userService) {
        this.imageRepository = imageRepository;
        this.communityRepository = communityRepository;
        this.userService = userService;
    }

    public void uploadImageToUser(MultipartFile file, Principal principal) throws IOException {
        User user = userService.getCurrentUser(principal);
        LOG.info("Uploading image profile to User {}", user.getId());

        ImageModel userProfileImage = imageRepository.findByUserIdAndIsBackground(user.getId(), false)
                .orElse(null);
        if (!ObjectUtils.isEmpty(userProfileImage)) {
            imageRepository.delete(userProfileImage);
        }
        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        imageModel.setIsBackground(false);
        imageRepository.save(imageModel);
    }

    public void uploadBackgroundToUser(MultipartFile file, Principal principal) throws IOException {
        User user = userService.getCurrentUser(principal);
        LOG.info("Uploading background to User {}", user.getId());

        ImageModel userBackgroundImage = imageRepository.findByUserIdAndIsBackground(user.getId(), true)
                .orElse(null);
        if (!ObjectUtils.isEmpty(userBackgroundImage)) {
            imageRepository.delete(userBackgroundImage);
        }
        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        imageModel.setIsBackground(true);
        imageRepository.save(imageModel);
    }

    public void uploadImageToCommunity(MultipartFile file, Principal principal, Long communityId) throws IOException {
        User user = userService.getCurrentUser(principal);
        Community community = communityRepository.getReferenceById(communityId);

        if (user.getId().equals(community.getCreator().getId())) {
            ImageModel communityImage = imageRepository.findByCommunityId(community.getId())
                    .orElse(null);

            if (!ObjectUtils.isEmpty(communityImage)) {
                imageRepository.delete(communityImage);
            }

            ImageModel imageModel = new ImageModel();
            imageModel.setCommunityId(community.getId());
            imageModel.setImageBytes(compressBytes(file.getBytes()));
            imageModel.setName(file.getOriginalFilename());
            LOG.info("Uploading image to Community {}", community.getId());
            imageRepository.save(imageModel);
        }
    }

    public void uploadImageToPost(MultipartFile file, Principal principal, Long postId) throws IOException {
        User user = userService.getCurrentUser(principal);
        List<Post> posts = new ArrayList<>(user.getPosts());
        List<Community> communities = communityRepository.findAllByOrderByName();

        List<Post> communityPostList = communities.stream()
                .flatMap(e -> e.getPosts().stream())
                .toList();

        posts.addAll(communityPostList);

        Post post = posts.stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSinglePostCollector());

        ImageModel imageModel = new ImageModel();
        imageModel.setPostId(post.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        LOG.info("Uploading image to Post {}", post.getId());
        imageRepository.save(imageModel);
    }

    public ImageModel getImageToUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        ImageModel imageModel = imageRepository.findByUserIdAndIsBackground(user.getId(), false).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getBackgroundImageToUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        ImageModel imageModel = imageRepository.findByUserIdAndIsBackground(user.getId(), true).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageToUserById(Long userId) {
        ImageModel imageModel = imageRepository.findByUserIdAndIsBackground(userId, false).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageToUserByUsername(String username) {
        User user= userService.getUserByUsername(username);
        ImageModel imageModel = imageRepository.findByUserIdAndIsBackground(user.getId(), false).orElse(null);

        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getBackgroundImageToUserByUsername(String username) {
        User user= userService.getUserByUsername(username);
        ImageModel imageModel = imageRepository.findByUserIdAndIsBackground(user.getId(), true).orElse(null);

        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageToPost(Long postId) {
        ImageModel imageModel = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Not found image to Post: " + postId));
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageToCommunity(Long communityId) {
        ImageModel imageModel = imageRepository.findByCommunityId(communityId)
                .orElseThrow(() -> new ImageNotFoundException("Not found image to Community: " + communityId));
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            LOG.error("cannot compress bytes");
        }
        return outputStream.toByteArray();
    }

    private static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            LOG.error("cannot decompress bytes");
        }
        return outputStream.toByteArray();
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
