package com.mtsolutions.domain.model;

import com.mtsolutions.domain.entity.User;

import java.util.List;

public record UserPage(
        List<User> users,
        int page,
        int size,
        long totalElements
) {
    public int totalPages() {
        if (this.size <= 0) {
            return 0;
        }
        return (int) Math.ceil(this.totalElements / (double) this.size);
    }
}
