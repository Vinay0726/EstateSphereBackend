package com.realestate.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:5173/")
@CrossOrigin(origins = "https://estatesphere.netlify.app/")
@RestController
@RequestMapping("/images")
public class ImageController {

    // No need for local file serving since images are now on Cloudinary
    // You can keep this endpoint for compatibility or remove it if the frontend directly uses Cloudinary URLs
    @GetMapping("/view/{fileName}")
    public ResponseEntity<String> viewImage(@PathVariable String fileName) {
        // Since images are stored in Cloudinary, return a message or redirect to Cloudinary URL
        return new ResponseEntity<>("Images are served directly from Cloudinary URLs", HttpStatus.OK);
    }
}