package com.thanh.foodOrder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanh.foodOrder.domain.Cart;
import com.thanh.foodOrder.domain.CartDetail;
import com.thanh.foodOrder.domain.CartItemDetail;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.dtos.request.CartItemRequestDTO;
import com.thanh.foodOrder.dtos.request.CartRequestDTO;
import com.thanh.foodOrder.dtos.response.CartDetailsDTO;
import com.thanh.foodOrder.repository.CartRepository;
import com.thanh.foodOrder.util.JwtUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long CART_TTL = 30;

    public CartService(CartRepository cartRepository,
            UserService userService,
            ProductService productService,
            @Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    // add product to cart with db
    // @Transactional
    // public void addProductsToCart(CartRequestDTO request) {

    // String email = JwtUtil.getCurrentUserLogin().orElseThrow();
    // User user = userService.getUserByEmail(email);
    // // 1. Lấy cart
    // Cart cart = user.getCart();
    // if (cart == null) {
    // cart = new Cart();
    // cart.setUser(user);
    // cart.setCartDetails(new ArrayList<>());
    // }
    // // 2. Loop qua từng item từ JSON

    // Product product = productService.getProductById(request.getProductId());

    // // check inventory of product
    // this.productService.checkQuantityProductBeforeAddToCart(product,
    // request.getQuantity());
    // // 3. Check product đã có trong cart chưa
    // Optional<CartDetail> existingItem = cart.getCartDetails()
    // .stream()
    // .filter(cd -> cd.getProduct().getId().equals(product.getId()))
    // .findFirst();

    // if (existingItem.isPresent()) {
    // // 4. Nếu có rồi → cộng thêm quantity
    // CartDetail cartDetail = existingItem.get();
    // cartDetail.setQuantity(
    // cartDetail.getQuantity() + request.getQuantity());
    // } else {
    // // 5. Nếu chưa có → tạo mới
    // CartDetail cartDetail = new CartDetail();
    // cartDetail.setCart(cart);
    // cartDetail.setProduct(product);
    // cartDetail.setQuantity(request.getQuantity());
    // cartDetail.setPrice(product.getPrice());

    // cart.getCartDetails().add(cartDetail);

    // }

    // cartRepository.save(cart);
    // }
    private String cartKey(Long id) {
        return "cart:user:" + id;
    }

    private String productField(Long productId) {
        return "product:" + productId;
    }
    // add product to cart with redis

    public void addProductsToCart(CartRequestDTO request) {

        String email = JwtUtil.getCurrentUserLogin().orElseThrow();
        User user = this.userService.getUserByEmail(email);
        String key = cartKey(user.getId());

        Product product = productService.getProductById(request.getProductId());

        productService.checkQuantityProductBeforeAddToCart(
                product, request.getQuantity());

        HashOperations<String, String, CartItemDetail> hashOps = redisTemplate.opsForHash();

        String field = productField(product.getId());

        CartItemDetail item = hashOps.get(key, field);

        if (item != null) {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            item = new CartItemDetail();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(request.getQuantity());
            item.setImageUrl(product.getImg());
        }

        hashOps.put(key, field, item);
        redisTemplate.expire(key, CART_TTL, TimeUnit.MINUTES);
    }

    // @Transactional
    // public void removeProductFromCart(Long productId) {

    // String email = JwtUtil.getCurrentUserLogin().orElseThrow();
    // User user = userService.getUserByEmail(email);

    // Cart cart = user.getCart();

    // cart.getCartDetails().removeIf(
    // cd -> cd.getProduct().getId().equals(productId));

    // cartRepository.save(cart);
    // }

    public void removeProductFromCart(Long productId) {
        String email = JwtUtil.getCurrentUserLogin().orElseThrow();
        User user = this.userService.getUserByEmail(email);
        String key = "cart:user:" + user.getId();

        HashOperations<String, String, CartItemDetail> hashOps = redisTemplate.opsForHash();

        Long deleted = hashOps.delete(key, productField(productId));
        log.info("Deleted fields = {}", deleted);

        // Gia hạn TTL nếu cart vẫn còn item
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))
                && hashOps.size(key) > 0) {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }

        // Nếu cart rỗng → xóa luôn key
        if (hashOps.size(key) == 0) {
            redisTemplate.delete(key);
        }
    }

    // public List<CartDetailsDTO> getAllCartDetail() {
    // String email = JwtUtil.getCurrentUserLogin().orElseThrow();
    // User user = userService.getUserByEmail(email);

    // Cart cart = user.getCart();
    // if (cart == null) {
    // return List.of();
    // }

    // // 3. Lấy cart details
    // List<CartDetail> cartDetails = cart.getCartDetails();

    // // 4. Map Entity -> DTO
    // List<CartDetailsDTO> result = new ArrayList<>();

    // for (CartDetail cd : cartDetails) {
    // CartDetailsDTO dto = new CartDetailsDTO();
    // dto.setId(cd.getId());
    // dto.setQuantity(cd.getQuantity());
    // dto.setPrice(cd.getPrice());
    // dto.setTotalPrice(cd.getPrice() * cd.getQuantity());

    // dto.setCartId(cart.getId());
    // dto.setUserId(user.getId());

    // // map product
    // Product p = cd.getProduct();
    // if (p != null) {
    // CartDetailsDTO.ProductInnerCartDetail pDto = new
    // CartDetailsDTO.ProductInnerCartDetail();
    // pDto.setId(p.getId());
    // pDto.setName(p.getName());
    // pDto.setPrice(p.getPrice());
    // pDto.setImg(p.getImg());

    // dto.setProductInnerCartDetail(pDto);

    // CartDetailsDTO.CategoryInnerCartDetail cate = new
    // CartDetailsDTO.CategoryInnerCartDetail();
    // cate.setId(p.getCategory().getId());
    // cate.setName(p.getCategory().getName());
    // dto.setCategoryInnerCartDetail(cate);
    // }

    // result.add(dto);
    // }

    // return result;
    // }
    public List<CartDetailsDTO> getAllCartDetail() {
        String email = JwtUtil.getCurrentUserLogin().orElseThrow();
        User user = userService.getUserByEmail(email);

        String redisKey = "cart:user:" + user.getId();
        HashOperations<String, String, CartItemDetail> hashOps = redisTemplate.opsForHash();

        Map<String, CartItemDetail> cartMap = hashOps.entries(redisKey);
        // 1️⃣ Redis MISS
        if (cartMap == null || cartMap.isEmpty()) {
            return List.of();
        }
        // 2️⃣ Redis HIT → map sang DTO
        List<CartDetailsDTO> result = new ArrayList<>();
        for (CartItemDetail item : cartMap.values()) {

            CartDetailsDTO dto = new CartDetailsDTO();
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setTotalPrice(item.getSubtotal());

            // map product
            CartDetailsDTO.ProductInnerCartDetail pDto = new CartDetailsDTO.ProductInnerCartDetail();
            pDto.setId(item.getProductId());
            pDto.setName(item.getProductName());
            pDto.setPrice(item.getPrice());
            pDto.setImg(item.getImageUrl());

            dto.setProductInnerCartDetail(pDto);

            result.add(dto);
        }

        return result;
    }

}
