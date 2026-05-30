package com.user_service.user_service.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
