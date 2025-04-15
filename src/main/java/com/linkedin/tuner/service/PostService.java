package com.linkedin.tuner.service;

import com.linkedin.tuner.model.Post;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {
    
    private final MongoTemplate mongoTemplate;
    
    public PostService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public Post savePost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus("DRAFT");
        return mongoTemplate.save(post);
    }
    
    public Post updatePostStatus(String id, String status) {
        Post post = mongoTemplate.findById(id, Post.class);
        if (post != null) {
            post.setStatus(status);
            if ("PUBLISHED".equals(status)) {
                post.setPublishedAt(LocalDateTime.now());
            }
            return mongoTemplate.save(post);
        }
        return null;
    }
} 