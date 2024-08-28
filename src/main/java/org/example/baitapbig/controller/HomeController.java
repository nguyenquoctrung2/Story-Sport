package org.example.baitapbig.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.baitapbig.model.Account;
import org.example.baitapbig.model.Category;
import org.example.baitapbig.model.Product;
import org.example.baitapbig.service.CategoryService;
import org.example.baitapbig.service.ProductService;
import org.example.baitapbig.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
    @GetMapping("/signin")
    public String login() {
        return "login";
    }
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable int id , Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "view_product";
    }
    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            Account userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);

        }

        List<Category> allActiveCategory = categoryService.getAllCategory();
        m.addAttribute("categorys", allActiveCategory);
    }
    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {

        List<Category> categories = categoryService.getAllCategory();
        List<Product> products = productService.getAllProducts();
        m.addAttribute("categories", categories);
        m.addAttribute("products", products);
        m.addAttribute("paramValue", category);
        return "product";
    }
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute Account user, @RequestParam("img") MultipartFile file, HttpSession session, HttpServletRequest request)
            throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        Account saveUser = userService.saveUser(user);

        // send mail code verification set isEnable=true
        String resetToken = UUID.randomUUID().toString();
        userService.updateUserResetToken(user.getEmail(), resetToken);
        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

//				System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Register successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/register";
    }
}
