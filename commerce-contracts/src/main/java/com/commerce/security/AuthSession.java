package com.commerce.security;

import java.util.List;

public record AuthSession(Long userId, String username, List<String> roles, List<String> permissions) {
}
