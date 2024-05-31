package com.dacs1.customer.controller;

import com.dacs1.library.service.RateProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rate-product")
public class RateProductController {

    @Autowired
    private RateProductService rateProductService;

    @PostMapping("/rate/{id}")
    public void rateProduct(@PathVariable("id") Long id,
                            @RequestParam("starRate") int starRate,
                            @RequestParam("contentRate") String contentRate,
                            @RequestParam("imagesRate")List<MultipartFile> imagesRate,
                            Principal principal,
                            Model model){

        rateProductService.addComment(id, principal.getName(), starRate, contentRate, imagesRate);


    }

    @PostMapping("/like-comment/{id}")
    public void likeComment(@PathVariable("id")Long id, Principal principal){
        rateProductService.likeComment(id, principal.getName());
    }

    public void editComment(){

    }

    @PostMapping("/delete-comment")
    public void deleteComment(Long idComment, Principal principal){
        rateProductService.deleteComment(idComment, principal.getName());

    }

}
