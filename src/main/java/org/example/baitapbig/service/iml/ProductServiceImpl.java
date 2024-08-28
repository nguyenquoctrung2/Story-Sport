package org.example.baitapbig.service.iml;

import org.example.baitapbig.model.Product;
import org.example.baitapbig.repository.ProductRepository;
import org.example.baitapbig.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @Override
    public Product getProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    @Override
    public Boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }
    @Override
    public Product updateProduct(Product product, MultipartFile image) {

        Product products = getProductById(product.getId());

        String imageName = image.isEmpty() ? products.getImage() : image.getOriginalFilename();

        products.setTitle(product.getTitle());
        products.setDescription(product.getDescription());
        products.setCategory(product.getCategory());
        products.setPrice(product.getPrice());
        products.setImage(imageName);

        Product updateProduct = productRepository.save(products);

        if (!ObjectUtils.isEmpty(updateProduct)) {

            if (!image.isEmpty()) {

                try {
                    File saveFile = new ClassPathResource("static/img").getFile();

                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                            + image.getOriginalFilename());
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return product;
        }
        return null;
    }
}
