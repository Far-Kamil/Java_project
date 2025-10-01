package com.example.demo.controller;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
public class MainController{
    private ProductService productService;
    public MainController (ProductService productService)
    {
        this.productService= productService;
    }
    @GetMapping("/products")
    private List<Product> getProducts(){
        return productService.getAll();
    }
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid Product product) {
        productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }   
    @GetMapping("/product/{id}")
    public ResponseEntity <Product> getProductById(@PathVariable Long id)
    {
        Product product = productService.getById(id);
        if(product!=null)
        {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);   
    } 
    @PutMapping("/products/{id}")
    public ResponseEntity <Product> edit(@PathVariable Long id, @RequestBody @Valid Product product) {
        Product p= productService.update(id, product);
        if (p!=null)
        {
            return ResponseEntity.ok(p);
        }
        else{
        return ResponseEntity.notFound().build();
    }
    }  @DeleteMapping("/products/{id}")
    public ResponseEntity <Void> delete(@PathVariable Long id){
        if (productService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        else
            return ResponseEntity.notFound().build();
    }
    @GetMapping
    public ResponseEntity <Object> getProducts(@RequestParam(required = false) String title, @RequestParam(required = false) Integer min, 
    @RequestParam(required = false) Integer max, @PageableDefault(page = 0, size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(productService.getByFilter(title, min, max, pageable));
    }
    
}