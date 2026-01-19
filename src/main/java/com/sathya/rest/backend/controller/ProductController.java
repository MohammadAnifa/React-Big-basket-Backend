package com.sathya.rest.backend.controller;

import com.sathya.rest.backend.ProductRepository;
import com.sathya.rest.backend.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/images/";

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam String category,
            @RequestParam MultipartFile image) throws IOException {

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        File destFile = new File(UPLOAD_DIR + fileName);
        image.transferTo(destFile);

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setImagepath("/products/images/" + fileName); // Use full backend path

        return ResponseEntity.ok(productRepository.save(product));
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(imagePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

