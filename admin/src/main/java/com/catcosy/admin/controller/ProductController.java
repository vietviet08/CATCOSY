package com.catcosy.admin.controller;

import com.catcosy.admin.helper.SetNameAndRoleToPage;
import com.catcosy.admin.utils.ExcelExporter;
import com.catcosy.library.dto.ProductDto;
import com.catcosy.library.enums.ObjectManage;
import com.catcosy.library.model.Brand;
import com.catcosy.library.model.Category;
import com.catcosy.library.model.ProductImage;
import com.catcosy.library.model.Size;
import com.catcosy.library.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class ProductController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private S3StorageService s3StorageService;

    @GetMapping("/products")
    public String productPage(@RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false) String search,
            Model model) {
        Page<ProductDto> products = null;

        int pageSize = 11;

        if (search != null && !search.isEmpty())
            products = productService.pageProductSearch(search, page, pageSize);
        else if (page == null) {
            products = productService.pageProduct(0, pageSize);
            page = 0;

        } else {
            products = productService.pageProduct(page, pageSize);
        }
        List<ProductImage> productImages = productImageService.findByIdProductUnique();

        Map<Long, String> images = new HashMap<>();
        if (!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                if (productImage.getUsingS3() != null && productImage.getUsingS3() && productImage.getS3Url() != null) {
                    String publicUrl = s3StorageService.getPublicUrl(productImage.getS3Url());
                    images.put(productImage.getProduct().getId(), publicUrl);
                } else {
                    images.put(productImage.getProduct().getId(), productImage.getImage());
                }
            }
        }

        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("imagesMap", images);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "products", adminService);
        return "products";
    }

    @GetMapping("/export-products")
    public void exportProduct(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelExporter excelExporter = new ExcelExporter(productService.getAllProduct());

        List<String> fieldsToExport = List.of("id",
                "name",
                "category",
                "costPrice",
                "salePrice",
                "description",
                "isActivated");
        excelExporter.export(response, ObjectManage.Products.name(), fieldsToExport);
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        model.addAttribute("title", "Add product");
        model.addAttribute("sizes", sizeService.findAllSize());
        model.addAttribute("categories", categoryService.findAllCategory());
        model.addAttribute("brands", brandService.findAllBrand());
        model.addAttribute("newProduct", new ProductDto());
        // SetNameAndRoleToPage.setNameAndRoleToPage(model, "add-product",
        // adminService);
        return "add-product";
    }

    @PostMapping("/save-product")
    public String addProduct(@ModelAttribute("newProduct") ProductDto productDto,
            @RequestParam(value = "photos[]", required = false) List<MultipartFile> listFiles,
            @RequestParam(value = "sizesChoose", required = false, defaultValue = "") String[] sizesSelected,
            RedirectAttributes attributes) {
        List<Long> selectedSizeIds = new ArrayList<>();

        if (sizesSelected != null && sizesSelected.length > 0 && !sizesSelected[0].isEmpty()) {
            for (String n : sizesSelected) {
                selectedSizeIds.add(Long.parseLong(n));
            }
        }

        try {
            // Debug statement
            System.out.println("Number of images received: " + (listFiles != null ? listFiles.size() : 0));
            if (listFiles != null) {
                for (int i = 0; i < listFiles.size(); i++) {
                    MultipartFile file = listFiles.get(i);
                    System.out.println("Image " + i + " is empty: " + (file.isEmpty() ? "Yes" : "No") + ", size: "
                            + file.getSize());
                }
            }

            productService.save(listFiles, selectedSizeIds, productDto);
            attributes.addFlashAttribute("success", "Add product successfully!");
        } catch (DataIntegrityViolationException e) {
            attributes.addFlashAttribute("warning", "Name product already exist!");
            e.printStackTrace();
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Add product failed: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/products";
    }

    @RequestMapping(value = "/findByIdProduct", method = { RequestMethod.GET, RequestMethod.PUT })
    @ResponseBody
    public ProductDto getById(Long id) {
        return productService.getById(id);
    }

    @GetMapping("/update-product/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model) {
        ProductDto productDto = productService.getById(id);
        List<Category> categoryList = categoryService.findAllCategoryIsActivate();

        List<Size> sizesExisting = new ArrayList<>();

        productDto.getSizes().forEach(productSize -> sizesExisting.add(productSize.getSize()));

        model.addAttribute("title", "Update product");
        model.addAttribute("productDto", productDto);
        model.addAttribute("sizes", sizeService.findAllSize());
        model.addAttribute("sizesExisting", sizesExisting);
        model.addAttribute("categories", categoryList);
        model.addAttribute("brands", brandService.findAllBrand());
        model.addAttribute("images", productDto.getImages());
        return "update-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
            @RequestParam(value = "newPhotos[]", required = false) List<MultipartFile> newImages,
            @RequestParam(value = "oldPhotos[]", required = false) List<String> oldImagesBase64,
            @RequestParam(value = "deletedImageIds[]", required = false) List<Integer> deletedImageIds,
            @RequestParam(value = "sizesChoose", required = false, defaultValue = "") String[] sizesSelected,
            RedirectAttributes attributes) {
        try {
            List<Long> selectedSizeIds = new ArrayList<>();

            if (sizesSelected != null && sizesSelected.length > 0 && !sizesSelected[0].isEmpty()) {
                for (String n : sizesSelected) {
                    selectedSizeIds.add(Long.parseLong(n));
                }
            }

            // Debug statements
            System.out.println("Update - Product ID: " + productDto.getId());
            System.out.println("Update - Number of new images: " + (newImages != null ? newImages.size() : 0));
            System.out.println("Update - Number of old images retained: " + (oldImagesBase64 != null ? oldImagesBase64.size() : 0));
            System.out.println("Update - Number of deleted image IDs: " + (deletedImageIds != null ? deletedImageIds.size() : 0));
            
            // Kiểm tra chi tiết các ảnh cũ để debug
            if (oldImagesBase64 != null && !oldImagesBase64.isEmpty()) {
                for (int i = 0; i < oldImagesBase64.size(); i++) {
                    String base64 = oldImagesBase64.get(i);
                    System.out.println("Old image " + i + " data length: " + (base64 != null ? base64.length() : 0));
                }
            }
            
            // Kiểm tra các hình ảnh mới
            if (newImages != null) {
                int validImageCount = 0;
                for (int i = 0; i < newImages.size(); i++) {
                    MultipartFile file = newImages.get(i);
                    boolean isEmpty = file.isEmpty();
                    System.out.println("Update - New image " + i + " is empty: " + (isEmpty ? "Yes" : "No")
                            + ", size: " + file.getSize());
                    
                    if (!isEmpty) {
                        validImageCount++;
                    }
                }
                System.out.println("Update - Total valid new images: " + validImageCount);
            }

            // Gọi service để cập nhật sản phẩm với khả năng giữ ảnh cũ và thêm ảnh mới
            productService.updateWithMixedImages(productDto, newImages, oldImagesBase64, deletedImageIds, selectedSizeIds);
            attributes.addFlashAttribute("success", "Update product successfully!");

        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Update product failed: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/products";
    }

    @RequestMapping(value = "/delete-product", method = { RequestMethod.GET, RequestMethod.PUT })
    public String deleteProduct(ProductDto product) {
        productService.deleteById(product.getId());
        return "redirect:/products";
    }

    @RequestMapping(value = "/activate-product", method = { RequestMethod.GET, RequestMethod.PUT })
    public String activateProduct(ProductDto product) {
        productService.activateById(product.getId());
        return "redirect:/products";
    }

    @GetMapping("/products/{pageNo}")
    public String pageProduct(@PathVariable("pageNo") int pageNo, Model model) {

        Page<ProductDto> products = productService.pageProduct(pageNo, 10);
        List<ProductImage> productImages = productImageService.findByIdProductUnique();

        Map<Long, String> images = new HashMap<>();
        if (!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                // Nếu sử dụng S3, lấy URL public
                if (productImage.getUsingS3() != null && productImage.getUsingS3() && productImage.getS3Url() != null) {
                    String publicUrl = s3StorageService.getPublicUrl(productImage.getS3Url());
                    images.put(productImage.getProduct().getId(), publicUrl);
                } else {
                    // Nếu không sử dụng S3, lấy Base64 data
                    images.put(productImage.getProduct().getId(), productImage.getImage());
                }
            }
        }

        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("imagesMap", images);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", products.getTotalPages());
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "products", adminService);
        return "products";
    }

    @GetMapping("/search-products/{pageNo}")
    public String pageProductSearch(@PathVariable("pageNo") int pageNo, @RequestParam("keyword") String key,
            Model model) {

        Page<ProductDto> products = productService.pageProductSearch(key, pageNo, 10);
        List<ProductImage> productImages = productImageService.findByIdProductUnique();

        Map<Long, String> images = new HashMap<>();
        if (!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                // Nếu sử dụng S3, lấy URL public
                if (productImage.getUsingS3() != null && productImage.getUsingS3() && productImage.getS3Url() != null) {
                    String publicUrl = s3StorageService.getPublicUrl(productImage.getS3Url());
                    images.put(productImage.getProduct().getId(), publicUrl);
                } else {
                    // Nếu không sử dụng S3, lấy Base64 data
                    images.put(productImage.getProduct().getId(), productImage.getImage());
                }
            }
        }

        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("imagesMap", images);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", products.getTotalPages());
        SetNameAndRoleToPage.setNameAndRoleToPage(model, "products", adminService);
        return "products";
    }

    @GetMapping("/sort-prices")
    public String sortPrice(@RequestParam("nameOption") int nameOption, Model model) {
        // switch (nameOption) {
        // case 1: {
        // model.addAttribute("productSortedDesc", productService.sortDesc());
        // }
        // case 2:
        // model.addAttribute("productSortedAsc", productService.sortAsc());
        // }
        return "products";
    }

    @GetMapping("/sort-category")
    public List<ProductDto> sortCategory(@RequestParam("nameOption") String nameOption) {
        return productService.byCategory(nameOption);
    }

}
