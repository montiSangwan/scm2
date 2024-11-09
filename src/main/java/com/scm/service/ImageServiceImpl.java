package com.scm.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helper.AppConstants;

@Service
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile contactImage, String fileName) {
        
        try {
            byte[] data = new byte[contactImage.getInputStream().available()];

            // update contact image content to byte array data
            contactImage.getInputStream().read(data);

            // upload data to cloudinary
            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                "public_id", fileName
            ));

            // return url of contact image to save in db
            return this.getUrlFromPublicId(fileName);

        } catch (IOException e) {
            e.printStackTrace();
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
            .crop(AppConstants.CONTACT_IMAGE_CROP)
        )
        .generate(publicId);
    }
    
}
