package com.scm.service;

import org.springframework.web.multipart.MultipartFile;

/*
 * this interface will upload the contact image data into byte[] on cloudinary and return url of image
 */
public interface ImageService {

    String uploadImage(MultipartFile contactImage, String fileName);

    String getUrlFromPublicId(String publicId);
    
}
