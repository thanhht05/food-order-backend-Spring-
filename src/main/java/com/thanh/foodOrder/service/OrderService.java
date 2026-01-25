package com.thanh.foodOrder.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanh.foodOrder.domain.CartDetail;
import com.thanh.foodOrder.domain.Order;
import com.thanh.foodOrder.domain.OrderDetail;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.dtos.CheckoutRequestDTO;
import com.thanh.foodOrder.dtos.OrderItemDTO;
import com.thanh.foodOrder.dtos.OrderResponseDTO;
import com.thanh.foodOrder.enums.OrderStatus;
import com.thanh.foodOrder.repository.CartDetailRepository;
import com.thanh.foodOrder.repository.CartRepository;
import com.thanh.foodOrder.repository.OrderDetailRepository;
import com.thanh.foodOrder.repository.OrderRepository;
import com.thanh.foodOrder.util.exception.CommonException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, CartDetailRepository cartDetailRepository,
            OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderDetailRepository = orderDetailRepository;

    }

    private OrderResponseDTO mapToOrderResponseDTO(
            Order order,
            List<OrderDetail> orderDetails) {

        OrderResponseDTO dto = new OrderResponseDTO();

        // Order information
        dto.setOrderId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getOrderStatus().name());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setDiscount(order.getDiscount());
        dto.setFinalPrice(order.getFinalPrice());

        // Order items
        List<OrderItemDTO> items = new ArrayList<>();

        for (OrderDetail od : orderDetails) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductId(od.getProduct().getId());
            itemDTO.setProductName(od.getProduct().getName());
            itemDTO.setQuantity(od.getQuantity());
            itemDTO.setPrice(od.getPrice());
            items.add(itemDTO);
        }

        dto.setItems(items);

        return dto;
    }

    private void validBeforePlaceOrder(CheckoutRequestDTO dto, User curUser, List<CartDetail> cartDetails) {

        // If no items are available for checkout, stop the process
        if (cartDetails.isEmpty()) {
            throw new CommonException("No available items to place order");
        }

        // 2. Validate CartDetail ownership and quantity
        for (CartDetail cd : cartDetails) {

            // Check whether the cart detail belongs to the current user
            if (!cd.getCart().getUser().getId().equals(curUser.getId())) {
                throw new CommonException("CartDetail does not belong to the current user");
            }

            // Validate item quantity
            if (cd.getQuantity() <= 0) {
                throw new CommonException("Item quantity is invalid");
            }
        }
    }

    private double caculateTotalPrice(List<CartDetail> cartDetails) {
        double totalPrice = 0;
        for (CartDetail cd : cartDetails) {
            totalPrice += cd.getQuantity() * cd.getPrice();
        }
        return totalPrice;
    }

    @Transactional
    public OrderResponseDTO placeOrder(CheckoutRequestDTO dto, User curUser) {
        // 1. Retrieve CartDetail list by IDs from request
        List<CartDetail> cartDetails = this.cartDetailRepository.findByIdIn(dto.getCartDetailIds());
        validBeforePlaceOrder(dto, curUser, cartDetails);

        // 3. Calculate total price using CartDetail data
        double totalPrice = caculateTotalPrice(cartDetails);

        // 4. Create and save Order
        Order order = new Order();
        order.setUser(curUser);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(totalPrice);
        order.setFinalPrice(totalPrice); // No voucher applied yet
        order.setOrderStatus(OrderStatus.PENDING);

        order = this.orderRepository.save(order);

        // 5. Create OrderDetail records from CartDetail
        for (CartDetail cd : cartDetails) {

            OrderDetail od = new OrderDetail();
            od.setOrder(order);
            od.setNote(dto.getNote()); // Common note for the whole order
            od.setProduct(cd.getProduct());
            od.setQuantity(cd.getQuantity());
            od.setPrice(cd.getPrice());

            this.orderDetailRepository.save(od);
        }

        // 6. Remove CartDetail after successful checkout
        this.cartDetailRepository.deleteAll(cartDetails);

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        OrderResponseDTO res = mapToOrderResponseDTO(order, orderDetails);
        return res;
    }

}
