package com.example.backend.controller.shop;

import com.example.backend.dto.product.AllProductDto;
import com.example.backend.service.ShopService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/shop")
@RestController
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/all")
    public Slice<AllProductDto> getTotalProduct(@RequestParam("pageNumber") int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, 20);
        return shopService.getTotalProduct(pageable);
    }

    @GetMapping("/main")
    public Slice<AllProductDto> getMainDepartment(@RequestParam("pageNumber") int pageNumber, @RequestParam(value = "mainDepartment", required = false) String mainDepartment) {
        String[] mainDepartments = null;
        if (mainDepartment.contains(",")) {
            mainDepartments = mainDepartment.split(",");
        } else {
            mainDepartments = new String[]{mainDepartment};
        }

        Pageable pageable = PageRequest.of(pageNumber, 20);
        return shopService.getMainDepartmentFilter(pageable, Arrays.asList(mainDepartments));
    }

    @GetMapping("/sub")
    public Slice<AllProductDto> getSubDepartment(@RequestParam("pageNumber") int pageNumber, @RequestParam(value = "subDepartment", required = false) String subDepartment) {

        String[] subDepartments = null;
        if (subDepartment.contains(",")) {
            subDepartments = subDepartment.split(",");
        } else {
            subDepartments = new String[]{subDepartment};
        }

        Pageable pageable = PageRequest.of(pageNumber, 20);
        return shopService.getSubDepartmentFilter(pageable, Arrays.asList(subDepartments));
    }

}
