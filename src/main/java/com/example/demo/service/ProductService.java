package com.example.demo.service;

import java.util.List;



import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.specification.ProductSpecification;



@Service
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository prodRep;
    
    public ProductService(ProductRepository prodRep) {
        this.prodRep = prodRep;
    }
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product create(Product product)
    {
        return prodRep.save(product);
    }
    @Cacheable(value = "products", key = "#root.methodName")
    public List<Product> getAll()
    {
        return prodRep.findAll();
    }
    @Cacheable(value = "products", key = "#id")
    public Product getById(Long id)
    {
        return prodRep.findById(id).orElse(null);
    }
    @Transactional
    public Product update(Long id, Product updated)
    {
        return prodRep.findById(id)
            .map(p -> {
            p.setTitle(updated.getTitle());
            p.setCost(updated.getCost());
            return prodRep.save(p);
        })
        .orElse(null);
    }
    @Transactional
    public boolean delete(Long id)
    {
        if (prodRep.existsById(id)) {
            prodRep.deleteById(id);
            return true;
        }
        return false;
    }
    public List <Product> getByTitle(String title)
    {
        if (title!=null) {
            return prodRep.findByTitleContainingIgnoreCase(title);
        }
        return prodRep.findAll();
    }
    public Page <Product> getByFilter(String title, Integer min, Integer max, Pageable pageable)
    {
        return prodRep.findAll(ProductSpecification.filter(title, min, max), pageable);
    }
    
}
