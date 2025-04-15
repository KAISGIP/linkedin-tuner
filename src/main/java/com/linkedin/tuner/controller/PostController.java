package com.linkedin.tuner.controller;

import com.linkedin.tuner.model.Post;
import com.linkedin.tuner.service.DeepSeekService;
import com.linkedin.tuner.service.LinkedInService;
import com.linkedin.tuner.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    
    private final DeepSeekService deepSeekService;
    private final LinkedInService linkedInService;
    private final PostService postService;
    
    public PostController(DeepSeekService deepSeekService, 
                         LinkedInService linkedInService,
                         PostService postService) {
        this.deepSeekService = deepSeekService;
        this.linkedInService = linkedInService;
        this.postService = postService;
    }
    
    @PostMapping("/correct")
    public ResponseEntity<?> correctPost(@RequestBody Map<String, Object> request) {
        try {
            logger.info("Début de la correction du post");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
            String content = messages.get(0).get("content");
            String correctedContent = deepSeekService.correctText(content);
            logger.info("Post corrigé avec succès");
            return ResponseEntity.ok(Map.of("correctedText", correctedContent));
        } catch (Exception e) {
            logger.error("Erreur lors de la correction du post", e);
            return ResponseEntity.badRequest().body("Erreur lors de la correction: " + e.getMessage());
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> savePost(@RequestBody Post post) {
        try {
            logger.info("Début de la sauvegarde du post");
            Post savedPost = postService.savePost(post);
            logger.info("Post sauvegardé avec succès");
            return ResponseEntity.ok(savedPost);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde du post", e);
            return ResponseEntity.badRequest().body("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }
    
    @PostMapping("/publish")
    public ResponseEntity<?> publishPost(@RequestBody Post post) {
        try {
            logger.info("Début de la publication du post");
            String linkedInPostId = linkedInService.publishPost(post.getCorrectedContent());
            post.setLinkedInPostId(linkedInPostId);
            postService.updatePostStatus(post.getId(), "PUBLISHED");
            logger.info("Post publié avec succès");
            return ResponseEntity.ok(linkedInPostId);
        } catch (Exception e) {
            logger.error("Erreur lors de la publication du post", e);
            return ResponseEntity.badRequest().body("Erreur lors de la publication: " + e.getMessage());
        }
    }
} 