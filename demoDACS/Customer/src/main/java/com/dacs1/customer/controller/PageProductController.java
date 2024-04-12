package com.dacs1.customer.controller;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Category;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.service.CategoryService;
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
import java.util.Optional;

@Controller
public class PageProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private CategoryService categoryService;

//    @GetMapping({"/all-product", "/products"})
//    public String getAllProduct(@RequestParam(required = false, defaultValue = "0") int page, Model model) {
//
//        model.addAttribute("title", " All Product");
//        Page<ProductDto> products = productService.pageProductIsActivated(page, 3);
//
//        model.addAttribute("products", products);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("size", products.getSize());
//        model.addAttribute("totalPage", products.getTotalPages());
//        return "page-product";
//    }

    @GetMapping("/products")
    public String getAllProductTest(Model model,
                                    @RequestParam(required = false, defaultValue = "0") int page,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String sortPrice,
                                    @RequestParam(required = false, defaultValue = "0") Long idCategory) {

        model.addAttribute("title", " All Product");

        List<Category> categoryList = categoryService.findAllCategoryIsActivate();
        model.addAttribute("categories", categoryList);

        if(idCategory != 0){
            String nameCategory = categoryService.getById(idCategory).getName();
            model.addAttribute("nameCategory", nameCategory);
        }else {
            model.addAttribute("nameCategory", "Products");
        }

        int pageSize = 3;

        Page<ProductDto> products = null;
        if(sortPrice == null) sortPrice = "";
        if(keyword == null) keyword = "";


        if (sortPrice.equals("true")) {
            products = productService.pageProductIsActivatedFilter(page, pageSize, keyword, "true", idCategory);
            model.addAttribute("keyword", keyword);
            model.addAttribute("sort", "true");
            model.addAttribute("idCategory", idCategory);
        } else if (sortPrice.equals("false")) {
            products = productService.pageProductIsActivatedFilter(page, pageSize, keyword, "false", idCategory);
            model.addAttribute("keyword", keyword);
            model.addAttribute("sort", "false");
            model.addAttribute("idCategory", idCategory);
        } else if (sortPrice.equals("news")){
            products = productService.pageProductIsActivatedFilter(page, pageSize, keyword, "news", idCategory);
            model.addAttribute("keyword", keyword);
            model.addAttribute("sort", "news");
            model.addAttribute("idCategory", idCategory);
        }else if(sortPrice.isEmpty() && !keyword.isEmpty()){
            products = productService.pageProductIsActivatedFilter(page, pageSize, keyword, "", idCategory);
            model.addAttribute("keyword", keyword);
            model.addAttribute("sort", "");
            model.addAttribute("idCategory", idCategory);
        }else if(sortPrice.isEmpty() && keyword.isEmpty()){
            products = productService.pageProductIsActivatedFilter(page, pageSize, "", "", idCategory);
            model.addAttribute("keyword", "");
            model.addAttribute("sort", "");
            model.addAttribute("idCategory", idCategory);
        }




//        if ((keyword == null || keyword.isEmpty()) && (sortPrice == null || sortPrice.isEmpty())) {
//            products = productService.pageProductIsActivated(page, 3);
//        }else if(sortPrice == null || sortPrice.isEmpty()){
//            products = productService.pageProductIsActivatedFilter(page, 3, keyword, false);
//        }else{
//            products = productService.pageProductIsActivatedFilter(page, 3, keyword, true);
//        }

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", products.getSize());
        model.addAttribute("totalPage", products.getTotalPages());
        return "page-product";
    }


    @GetMapping("/product-detail/{id}")
    public String allProduct(@PathVariable("id") long id, Model model) {

        ProductDto productDto = productService.getById(id);
        List<ProductDto> productsSameCategory = productService.productRandomSameCategoryLimit(productDto.getCategory().getId(), id);

        model.addAttribute("title", productDto.getName());
        model.addAttribute("product", productDto);
        model.addAttribute("productsSameCategory", productsSameCategory);


        return "detail-product";
    }


}
