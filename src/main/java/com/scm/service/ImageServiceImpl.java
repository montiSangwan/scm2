package com.scm.service;

import static com.scm.helper.AppConstants.MAX_FILE_SIZE;

import java.io.IOException;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helper.AppConstants;
import com.scm.helper.ImageValidationException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile contactImage, String fileName) {

        try {
            if (Objects.nonNull(contactImage) && contactImage.getInputStream().available() > 0) {

                byte[] data = new byte[contactImage.getInputStream().available()];

                // update contact image content to byte array data
                contactImage.getInputStream().read(data);

                // upload data to cloudinary
                cloudinary.uploader().upload(data, ObjectUtils.asMap(
                        "public_id", fileName));

                // return url of contact image to save in db
                return this.getUrlFromPublicId(fileName);
            } else {
                return AppConstants.DEFAULT_PROFILE_URL;
            }

        } catch (IOException e) {
            log.error("Error while uploading image to cloudinary: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                .crop(AppConstants.CONTACT_IMAGE_CROP))
                .generate(publicId);
    }

    @Override
    public void validateImage(MultipartFile image) throws ImageValidationException {
        // Check if the image file size is within the allowed limit
        if (image.getSize() > AppConstants.MAX_FILE_SIZE) {
            throw new ImageValidationException("Image file is too large. Maximum size allowed is %d bytes.", MAX_FILE_SIZE);
        }

        // Check if the image type is valid (jpg, jpeg, png)
        if (!this.isValidImageType(image)) {
            throw new ImageValidationException("Invalid image type. Supported types are: %s", "jpg, png, jpeg");
        }
    }

    // Method to validate the image type based on the MIME type
    private boolean isValidImageType(MultipartFile image) {
        // Get the MIME type of the image (like "image/jpeg" or "image/png")
        String contentType = image.getContentType();

        // Return true if the MIME type matches one of the supported types
        return AppConstants.SUPPORTED_IMAGE_TYPES.contains(contentType);
    }

}
