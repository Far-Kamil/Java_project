package com.example.demo.specification;
import com.example.demo.model.Product;

import org.hibernate.query.Page;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    
    private static Specification <Product> priceBetween(Integer min, Integer max)
    {
         return (root, query, criteriaBuilder) -> {
            if(min == null || max == null)
            {
                return null;
            }
            if(min !=null && max!= null)
            {
                return criteriaBuilder.between(root.get("price"), min, max);
            }
            if (min !=null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), min);
            }
             return criteriaBuilder.lessThanOrEqualTo(root.get("price"), max);
        };     
    }
    public static Specification <Product> filter(String title, Integer min, Integer max)
        {
            return Specification.allOf(titleLike(title), priceBetween(min, max));
        }
    
    private static Specification <Product> titleLike(String title)
    {
        return (root, query, criteriaBuilder) -> {
            if(title == null || title.trim().isEmpty())
            {
                return null;
            }
             return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%"+ title.toLowerCase().trim()+"%");
        };     
    }
}
