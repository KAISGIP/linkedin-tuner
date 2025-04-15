package com.linkedin.tuner.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String content;
    private String correctedContent;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private String linkedInPostId;
    private String userId;
} 