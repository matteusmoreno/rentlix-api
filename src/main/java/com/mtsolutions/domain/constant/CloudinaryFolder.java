package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum CloudinaryFolder {

    USER_PICTURE("rentlix/users/pictures", "image"),
    USER_DOCUMENT("rentlix/users/documents", "raw"),
    ASSET_IMAGE("rentlix/assets/images", "image"),
    ASSET_GALLERY("rentlix/assets/gallery", "image"),
    RENTAL_CONTRACT("rentlix/rentals/contracts", "raw");

    private final String path;
    private final String resourceType;

    CloudinaryFolder(String path, String resourceType) {
        this.path = path;
        this.resourceType = resourceType;
    }
}
