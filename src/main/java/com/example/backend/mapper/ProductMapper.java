package com.example.backend.mapper;

import com.example.backend.dto.response.ProductResponse;
import com.example.backend.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "categoryName", source = "category.name")
    List<ProductResponse> productsToProductResponses(List<Product> products);
}
