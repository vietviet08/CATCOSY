package com.catcosy.admin.controller;

import com.catcosy.library.model.RateProduct;
import com.catcosy.library.service.RateProductService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.Rate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
//@RequestMapping(path = "/product/comments")
@RequiredArgsConstructor
public class CommentController {

    private final RateProductService rateProductService;

    @GetMapping("/product/comments/{id}")
    public String getAllComment(@PathVariable("id") Long id, Model model) {
        List<RateProduct> rateProductList = rateProductService.getAllByIdProduct(id);
        model.addAttribute("title", "Comments Product");
        model.addAttribute("size", rateProductList.size());
        model.addAttribute("comments", rateProductList);
        model.addAttribute("idProduct", id);

        return "comments-product";
    }

    @PostMapping("/hide-comment/{idComment}")
    public String hideComment(@PathVariable("idComment") Long idComment,
                              @ModelAttribute("idProduct") Long idProduct,
                              RedirectAttributes attributes) {
        try {
            rateProductService.deleteCommentByAdmin(idComment);
            attributes.addFlashAttribute("success", "Delete comment successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("failure", "Delete comment error, maybe error from server!");
        }
        return "redirect:/product/comments/" + idProduct;
    }

    @PostMapping("/allow-comment/{idComment}")
    public String allowComment(@PathVariable("idComment") Long idComment,
                               @ModelAttribute("idProduct") Long idProduct,
                               RedirectAttributes attributes) {
        try {
            rateProductService.allowCommentByAdmin(idComment);
            attributes.addFlashAttribute("success", "Allow comment successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("failure", "Delete comment error, maybe error from server!");
        }
        return "redirect:/product/comments/" + idProduct;
    }

}

