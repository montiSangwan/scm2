package com.scm.helper;

import java.util.Arrays;
import java.util.List;

public class AppConstants {
    
    public static final String ROLE_USER = "ROLE_USER";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final Integer CONTACT_IMAGE_WIDTH = 500;

    public static final Integer CONTACT_IMAGE_HEIGHT = 500;

    public static final String CONTACT_IMAGE_CROP = "fill";

    public static final String DEFAULT_PROFILE_URL = "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png";

    public static final String SEARCH_BY_NAME = "name";

    public static final String SEARCH_BY_EMAIL = "email";

    public static final String SEARCH_BY_PHONE_NUMBER = "phoneNumber";

    // Define the maximum allowed image size (e.g., 5 MB)
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;  // 5 MB in bytes

    // Define supported image types
    public static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList("image/jpg", "image/jpeg", "image/png");
}
