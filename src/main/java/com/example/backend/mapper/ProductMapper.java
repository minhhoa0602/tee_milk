package com.example.backend.mapper;

import com.example.backend.dto.response.ProductOptionsResponse;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.entity.Product;
import com.example.backend.entity.Size;
import com.example.backend.entity.Topping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "categoryName", source = "category.name")
    List<ProductResponse> productsToProductResponses(List<Product> products);

    @Mapping(target = "productName",source = "product.name")
    @Mapping(target = "productId",source = "product.id")
    @Mapping(target = "sizes",source = "sizes")
    @Mapping(target = "sugarLevels",source = "sugarLevels")
    @Mapping(target = "iceLevels",source = "iceLevels")
    @Mapping(target = "toppings",source = "toppings")
    ProductOptionsResponse productToProductOptionsResponse(Product product, List<Size> sizes, List<String> sugarLevels, List<String> iceLevels, List<Topping> toppings);
}
