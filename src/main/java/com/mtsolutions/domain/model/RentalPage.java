package com.mtsolutions.domain.model;

import com.mtsolutions.domain.entity.Rental;

import java.util.List;

public record RentalPage(
        List<Rental> rentals,
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
