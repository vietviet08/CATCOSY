package com.dacs1.customer.controller;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.service.ProductImageService;
import com.dacs1.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PageProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @GetMapping({"/all-product", "/products"})
    public String getAllProduct(@RequestParam(required = false,defaultValue = "0") int page, Model model){

        model.addAttribute("title"," All Product");
        Page<ProductDto> products = productService.pageProductIsActivated(page, 3);

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", products.getSize());
        model.addAttribute("totalPage", products.getTotalPages());
        return "page-product";
    }

    @GetMapping("/product-detail/{id}")
    public String allProduct(@PathVariable("id") long id, Model model){

        ProductDto productDto = productService.getById(id);
        List<ProductDto> productsSameCategory = productService.productRandomSameCategoryLimit(productDto.getCategory().getId(), id);

        model.addAttribute("title", productDto.getName());
        model.addAttribute("product", productDto);
        model.addAttribute("productsSameCategory", productsSameCategory);


        return "detail-product";
    }


}
