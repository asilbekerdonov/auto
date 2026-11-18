package com.example.auto.product.web;

import com.example.auto.product.domain.Product;
import com.example.auto.product.service.ProductService;
import com.example.auto.product.web.dto.ProductResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(ProductResponseDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.of(productService.getProductById(id).map(ProductResponseDto::new));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] photo = product.get().getPhoto();
        if (photo == null || photo.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(detectContentType(photo))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"photo\"")
                .body(photo);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String carBrand,
            @RequestParam BigDecimal price,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setCarBrand(carBrand);
        product.setPrice(price);

        if (photo != null && !photo.isEmpty()) {
            product.setPhoto(photo.getBytes());
        }

        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductResponseDto(savedProduct));
    }

    private static MediaType detectContentType(byte[] photo) {
        if (photo.length >= 3
                && (photo[0] & 0xFF) == 0xFF
                && (photo[1] & 0xFF) == 0xD8
                && (photo[2] & 0xFF) == 0xFF) {
            return MediaType.IMAGE_JPEG;
        }

        if (photo.length >= 8
                && (photo[0] & 0xFF) == 0x89
                && photo[1] == 'P'
                && photo[2] == 'N'
                && photo[3] == 'G') {
            return MediaType.IMAGE_PNG;
        }

        if (photo.length >= 6
                && photo[0] == 'G'
                && photo[1] == 'I'
                && photo[2] == 'F'
                && photo[3] == '8') {
            return MediaType.IMAGE_GIF;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
