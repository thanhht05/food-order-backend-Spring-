package com.thanh.foodOrder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanh.foodOrder.domain.Cart;
import com.thanh.foodOrder.domain.CartDetail;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.dtos.request.CartItemRequestDTO;
import com.thanh.foodOrder.dtos.request.CartRequestDTO;
import com.thanh.foodOrder.dtos.response.CartDetailsDTO;
import com.thanh.foodOrder.repository.CartRepository;
import com.thanh.foodOrder.util.JwtUtil;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, UserService userService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Transactional
    public void addProductsToCart(CartRequestDTO request) {

        String email = JwtUtil.getCurrentUserLogin().orElseThrow();
        User user = userService.getUserByEmail(email);
        // 1. Lấy cart
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartDetails(new ArrayList<>());
        }
        // 2. Loop qua từng item từ JSON

        Product product = productService.getProductById(request.getProductId());

        // check inventory of product
        this.productService.checkQuantityProductBeforeAddToCart(product, request.getQuantity());
        // 3. Check product đã có trong cart chưa
        Optional<CartDetail> existingItem = cart.getCartDetails()
                .stream()
                .filter(cd -> cd.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // 4. Nếu có rồi → cộng thêm quantity
            CartDetail cartDetail = existingItem.get();
            cartDetail.setQuantity(
                    cartDetail.getQuantity() + request.getQuantity());
        } else {
            // 5. Nếu chưa có → tạo mới
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProduct(product);
            cartDetail.setQuantity(request.getQuantity());
            cartDetail.setPrice(product.getPrice());

            cart.getCartDetails().add(cartDetail);

        }

        cartRepository.save(cart);
    }

    @Transactional
    public void removeProductFromCart(Long productId) {

        String email = JwtUtil.getCurrentUserLogin().orElseThrow();
        User user = userService.getUserByEmail(email);

        Cart cart = user.getCart();

        cart.getCartDetails().removeIf(
                cd -> cd.getProduct().getId().equals(productId));

        cartRepository.save(cart);
    }

    public List<CartDetailsDTO> getAllCartDetail() {
        String email = JwtUtil.getCurrentUserLogin().orElseThrow();
        User user = userService.getUserByEmail(email);

        Cart cart = user.getCart();
        if (cart == null) {
            return List.of();
        }

        // 3. Lấy cart details
        List<CartDetail> cartDetails = cart.getCartDetails();

        // 4. Map Entity -> DTO
        List<CartDetailsDTO> result = new ArrayList<>();

        for (CartDetail cd : cartDetails) {
            CartDetailsDTO dto = new CartDetailsDTO();
            dto.setId(cd.getId());
            dto.setQuantity(cd.getQuantity());
            dto.setPrice(cd.getPrice());
            dto.setTotalPrice(cd.getPrice() * cd.getQuantity());

            dto.setCartId(cart.getId());
            dto.setUserId(user.getId());

            // map product
            Product p = cd.getProduct();
            if (p != null) {
                CartDetailsDTO.ProductInnerCartDetail pDto = new CartDetailsDTO.ProductInnerCartDetail();
                pDto.setId(p.getId());
                pDto.setName(p.getName());
                pDto.setPrice(p.getPrice());
                pDto.setImg(p.getImg());

                dto.setProductInnerCartDetail(pDto);

                CartDetailsDTO.CategoryInnerCartDetail cate = new CartDetailsDTO.CategoryInnerCartDetail();
                cate.setId(p.getCategory().getId());
                cate.setName(p.getCategory().getName());
                dto.setCategoryInnerCartDetail(cate);
            }

            result.add(dto);
        }

        return result;
    }

}
