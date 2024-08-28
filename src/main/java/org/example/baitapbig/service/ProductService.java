package org.example.baitapbig.service;

import org.example.baitapbig.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts();
    public Product getProductById(Integer id);
    public Product saveProduct(Product product);
    public Boolean deleteProduct(Integer id);
    public Product updateProduct(Product product, MultipartFile image);

}
