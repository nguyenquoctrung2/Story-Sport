package org.example.baitapbig.controller;

import org.example.baitapbig.model.Account;
import org.example.baitapbig.model.Category;
import org.example.baitapbig.service.CategoryService;
import org.example.baitapbig.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService UserService;
    @Autowired
    private CategoryService CategoryService;
    @GetMapping("/")
    public String home() {
        return "user/home";
    }
    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            Account userDtls = UserService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
           // Integer countCart = cartService.getCountCart(userDtls.getId());
          //  m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategory = CategoryService.getAllCategory();
        m.addAttribute("categorys", allActiveCategory);
    }
}
