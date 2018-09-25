package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("")
    public ModelAndView showAllCategories() {
        ModelAndView modelAndView;

        Iterable<Category> categories = categoryService.findAll();
        if (categories != null) {
            modelAndView = new ModelAndView("/category/list");
            modelAndView.addObject("categories", categories);
        } else {
            modelAndView = new ModelAndView("/error-404");
        }
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/category/create");
        modelAndView.addObject("category", new Category());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createNewCategory(@ModelAttribute("category") Category category) {
        ModelAndView modelAndView = new ModelAndView("/category/create");
        categoryService.save(category);
        modelAndView.addObject("category", new Category());
        modelAndView.addObject("msg", "New category has been created");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        ModelAndView modelAndView;
        Category category = categoryService.findById(id);
        if (category != null) {
            modelAndView = new ModelAndView("/category/edit");
            modelAndView.addObject("category", category);

        } else {
            modelAndView = new ModelAndView("/error-404");
        }

        return modelAndView;

    }

    @PostMapping("/edit/{id}")
    public ModelAndView updateCategory(@ModelAttribute("category") Category category) {
        categoryService.save(category);
        ModelAndView modelAndView = new ModelAndView("/category/edit");
        modelAndView.addObject("category", category);
        modelAndView.addObject("msg", "Category has been updated!");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView showBeforeDelete(@PathVariable("id") Long id) {
        Category category = categoryService.findById(id);
        if(category!= null) {
            ModelAndView modelAndView = new ModelAndView("/category/delete");
            modelAndView.addObject("category", category);
            return modelAndView;

        }else {
            ModelAndView modelAndView = new ModelAndView("/error-404");
            return modelAndView;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        Category category = categoryService.findById(id);
        categoryService.remove(category.getId());
        return "redirect:categories";
    }
}
