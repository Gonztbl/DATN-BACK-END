package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Product;
import com.vti.springdatajpa.repository.ProductRepository;
import com.vti.springdatajpa.repository.CategoryRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    public ProductPageResponseDTO getProducts(int page, int limit, String search, Integer categoryId,
            String restaurantId, String status, String sortBy, String sortDir) {
        // Convert 1-based page to 0-based for Spring Data
        int zeroBasedPage = page > 0 ? page - 1 : 0;

        // Validate sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Create pageable with sort
        Pageable pageable = PageRequest.of(zeroBasedPage, limit, Sort.by(direction, sortBy));

        // Get products with filters
        Page<Product> productPage = productRepository.findProductsWithFilters(
                categoryId, restaurantId, search, status, pageable);

        // Convert to DTOs
        List<ProductDetailDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList());

        // Build response
        ProductPageResponseDTO response = new ProductPageResponseDTO();
        response.setData(productDTOs);

        ProductPageResponseDTO.PaginationInfo pagination = new ProductPageResponseDTO.PaginationInfo();
        pagination.setTotal(productPage.getTotalElements());
        pagination.setPage(page);
        pagination.setLimit(limit);
        pagination.setTotalPages(productPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    public ProductDetailDTO getProductById(Integer id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        return convertToDetailDTO(product);
    }

    public ProductDetailDTO createProduct(ProductCreateRequestDTO request) {
        // Validate category exists
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new RuntimeException("Category not found with id: " + request.getCategoryId());
        }

        // Validate restaurant exists
        if (!restaurantRepository.existsById(request.getRestaurantId())) {
            throw new RuntimeException("Restaurant not found with id: " + request.getRestaurantId());
        }

        // Check if product name already exists
        if (productRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new RuntimeException("Product name already exists: " + request.getName());
        }

        Product product = modelMapper.map(request, Product.class);
        product.setRatingAvg(java.math.BigDecimal.ZERO);
        product.setRatingCount(0);

        Product savedProduct = productRepository.save(product);

        return convertToDetailDTO(savedProduct);
    }

    public ProductDetailDTO updateProduct(Integer id, ProductUpdateRequestDTO request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        System.out.println("Updating product with ID: " + id + ", Existing name: " + product.getName());

        // Check if name already exists (excluding current product)
        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (productRepository.existsByNameAndIdNotAndDeletedAtIsNull(request.getName(), id)) {
                throw new RuntimeException("Product name already exists: " + request.getName());
            }
            product.setName(request.getName());
        }

        // Validate category if provided
        if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategoryId())) {
            if (!categoryRepository.existsById(request.getCategoryId())) {
                throw new RuntimeException("Category not found with id: " + request.getCategoryId());
            }
            product.setCategoryId(request.getCategoryId());
            // Clear the associated object to force JPA to use the new categoryId column
            // value
            // since the JoinColumn is read-only
            product.setCategory(null);
        }

        // Validate restaurant if provided
        if (request.getRestaurantId() != null && !request.getRestaurantId().equals(product.getRestaurantId())) {
            if (!restaurantRepository.existsById(request.getRestaurantId())) {
                throw new RuntimeException("Restaurant not found with id: " + request.getRestaurantId());
            }
            product.setRestaurantId(request.getRestaurantId());
            // Clear the associated object to force JPA to use the new restaurantId column
            // value
            // since the JoinColumn is read-only
            product.setRestaurant(null);
        }

        // Update other fields if provided
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(java.math.BigDecimal.valueOf(request.getPrice()));
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getImageBase64() != null) {
            product.setImageBase64(request.getImageBase64());
        }

        Product savedProduct = productRepository.save(product);
        System.out.println("Product saved. ID: " + savedProduct.getId());

        return convertToDetailDTO(savedProduct);
    }

    public void deleteProduct(Integer id) {
        // Try both methods to find the product
        Product product = productRepository.findActiveProductById(id)
                .or(() -> productRepository.findByIdAndDeletedAtIsNull(id))
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Soft delete
        System.out.println("Before delete - deletedAt: " + product.getDeletedAt());
        product.setDeletedAt(java.time.LocalDateTime.now());
        System.out.println("After set - deletedAt: " + product.getDeletedAt());

        Product saved = productRepository.save(product);
        System.out.println("After save - deletedAt: " + saved.getDeletedAt());
        System.out.println("Product deleted successfully with ID: " + id);
    }

    public ProductDetailDTO updateProductStatus(Integer id, String status) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setStatus(status.toLowerCase());
        Product savedProduct = productRepository.save(product);
        return convertToDetailDTO(savedProduct);
    }

    private ProductDetailDTO convertToDetailDTO(Product product) {
        ProductDetailDTO dto = modelMapper.map(product, ProductDetailDTO.class);

        // Convert price from BigDecimal to Double
        if (product.getPrice() != null) {
            dto.setPrice(product.getPrice().doubleValue());
        }

        // Set category info
        if (product.getCategory() != null) {
            ProductDetailDTO.CategoryDTO categoryDTO = new ProductDetailDTO.CategoryDTO();
            categoryDTO.setId(product.getCategory().getId());
            categoryDTO.setName(product.getCategory().getName());
            categoryDTO.setIcon(product.getCategory().getIcon());
            dto.setCategory(categoryDTO);
        }

        // Set restaurant info
        if (product.getRestaurant() != null) {
            ProductDetailDTO.RestaurantDTO restaurantDTO = new ProductDetailDTO.RestaurantDTO();
            restaurantDTO.setId(product.getRestaurant().getId());
            restaurantDTO.setName(product.getRestaurant().getName());
            restaurantDTO.setLogoBase64(product.getRestaurant().getLogoBase64());
            dto.setRestaurant(restaurantDTO);
        }

        // Set imageBase64 from entity
        dto.setImageBase64(product.getImageBase64());

        // Set rating fields
        dto.setRatingAvg(product.getRatingAvg());
        dto.setRatingCount(product.getRatingCount());

        return dto;
    }
}
