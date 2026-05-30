package com.user_service.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
}
