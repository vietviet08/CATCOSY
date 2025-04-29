package com.catcosy.customer.controller;

import com.catcosy.library.model.RateProduct;
import com.catcosy.library.service.RateProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/detail-product")
public class RateProductRestController {

    @Autowired
    private RateProductService rateProductService;

    @PostMapping("/like-comment")
    public ResponseEntity<Integer> likeComment(@RequestParam("idComment") Long id, Principal principal) {
        if(principal == null ) return ResponseEntity.ok(0);
        RateProduct rateProduct = rateProductService.likeComment(id, principal.getName());
        if (rateProduct != null)
            return ResponseEntity.ok(rateProduct.getAmountOfLike());
        else return ResponseEntity.ok(rateProductService.getByIdComment(id).getAmountOfLike());
    }

}
