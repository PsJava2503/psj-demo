package com.commerce.user.interfaces.rest;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
