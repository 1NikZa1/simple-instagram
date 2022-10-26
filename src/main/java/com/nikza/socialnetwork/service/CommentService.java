package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.dto.CommentDTO;
import com.nikza.socialnetwork.entity.Comment;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.exceptions.PostNotFoundException;
import com.nikza.socialnetwork.repository.CommentRepository;
import com.nikza.socialnetwork.repository.PostRepository;
import com.nikza.socialnetwork.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    public static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found for user " + user.getId()));
        LOG.info("Saving comment for Post {}", post.getId());

        Comment comment = Comment.builder()
                .post(post)
                .userId(user.getId())
                .username(user.getUsername())
                .message(commentDTO.getMessage())
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
        return commentRepository.findAllByPost(post);
    }

    public void deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
        LOG.info("Comment {} successfully deleted", commentId);
    }
}
