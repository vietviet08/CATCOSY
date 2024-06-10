package com.dacs1.customer.controller;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.*;
import com.dacs1.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PageProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RateProductService rateProductService;

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
                                    @RequestParam(required = false, defaultValue = "0") Long idCategory,
                                    @RequestParam(required = false) String priceCdt,
                                    @RequestParam(required = false) String bySize) {

        List<Category> categoryList = categoryService.findAllCategoryIsActivate();
        model.addAttribute("categories", categoryList);

        if (idCategory != 0) {
            String nameCategory = categoryService.getById(idCategory).getName();
            model.addAttribute("nameCategory", nameCategory);
            model.addAttribute("title", nameCategory);
        } else {
            model.addAttribute("title", "All Product");
            model.addAttribute("nameCategory", "Products");
        }

        int pageSize = 18;

        Page<ProductDto> products = null;
        if (sortPrice == null) sortPrice = "";
        if (keyword == null) keyword = "";

        Integer minPrice = null;
        Integer maxPrice = null;
        if (priceCdt != null && !priceCdt.isEmpty()) {
            String[] slicePrice = priceCdt.split(":");
            if (slicePrice.length == 2) {
                minPrice = Integer.parseInt(slicePrice[0]);
                maxPrice = Integer.parseInt(slicePrice[1]);
            }
        }

        List<Long> listSizeId = sizeService.getAllSizeId();
        model.addAttribute("sizesId", listSizeId);
        List<Long> listSize = new ArrayList<>();

        if (bySize != null && !bySize.isEmpty()) {
            String[] sizesArray = bySize.split(",");
            for (String size : sizesArray) {
                listSize.add(Long.parseLong(size.trim()));
            }
        } else listSize.add(0L);

        products = productService.pageProductIsActivatedFilter(page, pageSize, keyword, sortPrice, idCategory, minPrice, maxPrice, listSize);

        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sortPrice);
        model.addAttribute("idCategory", idCategory);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sizeChoose", listSize);
        model.addAttribute("sizeChooseString", bySize);

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", products.getSize());
        model.addAttribute("totalPage", products.getTotalPages());
        return "page-product";
    }


    @GetMapping("/product-detail/{id}")
    public String allProduct(@PathVariable("id") long id, Model model, Principal principal) {

        ProductDto productDto = productService.getById(id);
        List<ProductDto> productsSameCategory = productService.productRandomSameCategoryLimit(productDto.getCategory().getId(), id, 8);
        boolean allowComment = false;
        if (principal != null)
            allowComment = orderDetailService.checkAllowComment(customerService.finByUsernameIsActive(principal.getName()).getId(), id);

//        boolean checkLikedComment = rateProductService.checkLikedComment(principal.getName(), id);

        List<RateProduct> rateProducts = rateProductService.getAllByIdProduct(id);


        double EStar = 0;

        double star5Percent = 0.0;
        double star4Percent = 0.0;
        double star3Percent = 0.0;
        double star2Percent = 0.0;
        double star1Percent = 0.0;

        int star5 = 0, star4 = 0, star3 = 0, star2 = 0, star1 = 0;

        double totalStar = 0;

        if (rateProducts != null && !rateProducts.isEmpty()) {
            int sizeRate = rateProducts.size();
            for (RateProduct rateProduct : rateProducts) {
                int star = rateProduct.getStar();
                totalStar += star;
                switch (star) {
                    case 5:
                        star5Percent++;
                        continue;
                    case 4:
                        star4Percent++;
                        continue;
                    case 3:
                        star3Percent++;
                        continue;
                    case 2:
                        star2Percent++;
                        continue;
                    case 1:
                        star1Percent++;
                        continue;
                }
            }

            star5 = (int) Math.round(((star5Percent / sizeRate) * 100));
             star4 = (int) Math.round(((star4Percent / sizeRate) * 100));
             star3 = (int) Math.round(((star3Percent / sizeRate) * 100));
             star2 = (int) Math.round(((star2Percent / sizeRate) * 100));
             star1 = (int) Math.round(((star1Percent / sizeRate) * 100));

            EStar = Math.round((totalStar / sizeRate) * 10);


            int rounded = (int) (Math.round(EStar / 5.0) * 5);
            int difference = (int) (EStar - rounded);

            if (Math.abs(difference) <= 2) {
                EStar = rounded;
            } else {
                EStar = rounded + (difference > 0 ? 5 : -5);
            }

            EStar /= 10;

        }
        model.addAttribute("title", productDto.getName());
        model.addAttribute("product", productDto);
        model.addAttribute("idProduct", id);
        model.addAttribute("productsSameCategory", productsSameCategory);
        model.addAttribute("allowComment", allowComment);
        model.addAttribute("EStar", EStar);
        model.addAttribute("star5Percent", star5);
        model.addAttribute("star4Percent", star4);
        model.addAttribute("star3Percent", star3);
        model.addAttribute("star2Percent", star2);
        model.addAttribute("star1Percent", star1);
        model.addAttribute("rateProducts", rateProducts);


        return "detail-product";
    }


}
