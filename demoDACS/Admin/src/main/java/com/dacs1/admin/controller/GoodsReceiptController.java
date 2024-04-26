package com.dacs1.admin.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoodsReceiptController {


    @GetMapping("/goods-receipt")
    public String  goodsReceiptPage(){

        

        return "goods-receipt";
    }
}
