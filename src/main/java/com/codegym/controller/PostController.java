package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Post;
import com.codegym.model.PostForm;
import com.codegym.service.CategoryService;
import com.codegym.service.PostService;
import com.codegym.utils.StorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    private Environment enviroment;

    @ModelAttribute("categories")
    public Iterable<Category> categories() {
        return categoryService.findAll();
    }

    @GetMapping("/create")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/post/create");
        modelAndView.addObject("postForm", new PostForm());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView saveNewPost(@ModelAttribute("postForm") PostForm postForm) {
        String randomFileName = "";
        String originalFileName = postForm.getImage().getOriginalFilename();
        if (!originalFileName.isEmpty()) {
            randomFileName = StorageUtils.generateRandomFileName(originalFileName);
            try {
                postForm.getImage().transferTo(new File(enviroment.getProperty("image_location") + randomFileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Date now = new Date();
        Post post = new Post(
                postForm.getTitle(),
                postForm.getDescription(),
                postForm.getContent(),
                randomFileName,
                now,
                postForm.getCategory()
        );
        postService.save(post);

        ModelAndView modelAndView = new ModelAndView("/post/create");
        modelAndView.addObject("postForm", new PostForm());
        modelAndView.addObject("msg", "New post has been created successfully!");
        return modelAndView;
    }

    @GetMapping("")
    public ModelAndView allPosts() {
        Iterable<Post> posts = postService.findAll();
        ModelAndView modelAndView = new ModelAndView("/post/list");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable("id") Long id) {
        Post post = postService.findById(id);

        PostForm postForm = new PostForm();
        postForm.setId(post.getId());
        postForm.setTitle(post.getTitle());
        postForm.setDescription(post.getDescription());
        postForm.setContent(post.getContent());
        postForm.setCategory(post.getCategory());
        postForm.setImgUrl(post.getImgUrl());

        ModelAndView modelAndView;

        if (post != null) {
            modelAndView = new ModelAndView("/post/edit");
            modelAndView.addObject("postForm", postForm);
        } else {
            modelAndView = new ModelAndView("/error-404");
        }
        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView updatePost(@ModelAttribute("postForm") PostForm postForm) {
        Post post = new Post();

        String originalFileName = postForm.getImage().getOriginalFilename();
        if(!originalFileName.isEmpty()) {
            try {
                StorageUtils.removeImage(enviroment.getProperty("image_upload_location") + post.getImgUrl());
                String randomFileName =  StorageUtils.generateRandomFileName(originalFileName);
                postForm.getImage().transferTo(new File(enviroment.getProperty("image_upload_location") + randomFileName));
                post.setImgUrl(randomFileName);
                postForm.setImgUrl(randomFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        post.setTitle(postForm.getTitle());
        post.setDescription(postForm.getDescription());
        post.setContent(postForm.getContent());
        post.setPostedDate(new Date());
        post.setCategory(postForm.getCategory());
        postService.save(post);

        ModelAndView modelAndView = new ModelAndView("/post/edit");
        modelAndView.addObject("postForm", postForm);
        modelAndView.addObject("msg", "Post has been updated");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable("id") Long id) {
        Post post = postService.findById(id);
        if (post != null) {
            ModelAndView modelAndView = new ModelAndView("/post/delete");
            modelAndView.addObject("post", post);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("/error-404");
            return modelAndView;
        }
    }

    @PostMapping("/delete")
    public String deletePost(@ModelAttribute("post") Post post) {
        postService.remove(post.getId());
        return "redirect:posts";
    }

}
