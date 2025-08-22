package com.pottermania.pottermania;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlogController {

    private final BlogPostRepository blogPostRepository;


    public BlogController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("titulo", "Blog - Pottermania");
        // Obtiene todas las publicaciones del blog
        List<BlogPost> todasLasPublicaciones = blogPostRepository.findAll();
        model.addAttribute("todasLasPublicaciones", todasLasPublicaciones);
        return "blog";
    }
}