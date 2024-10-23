package com.catcosy.customer.controller;

import com.catcosy.library.service.RateProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
public class RateProductController {

    @Autowired
    private RateProductService rateProductService;

    @PostMapping("/rate-product")
    public String rateProduct(@RequestParam(value = "idProduct") Long idProduct,
                              @RequestParam("idOrderDetail") Long idOrderDetail,
                              @RequestParam(value = "starRate") int starRate,
                              @RequestParam(value = "contentRate", required = false) String contentRate,
                              @RequestParam(value = "photos[]") List<MultipartFile> imagesRate,
                              Principal principal,
                              Model model) {

        rateProductService.addComment(idProduct, idOrderDetail, principal.getName(), starRate, contentRate, imagesRate);

        return "redirect:/orders";
    }



    public void editComment() {

    }

    @PostMapping("/delete-comment")
    public void deleteComment(Long idComment, Principal principal) {
        rateProductService.deleteComment(idComment, principal.getName());

    }

}
