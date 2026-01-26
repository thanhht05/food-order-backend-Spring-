package com.thanh.foodOrder.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanh.foodOrder.domain.BookingTable;
import com.thanh.foodOrder.domain.CartDetail;
import com.thanh.foodOrder.domain.Order;
import com.thanh.foodOrder.domain.OrderDetail;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.respone.order.OrderItemDTO;
import com.thanh.foodOrder.domain.respone.order.OrderResponseDTO;
import com.thanh.foodOrder.domain.respone.order.admin.AdminOrderResponseDTO;
import com.thanh.foodOrder.dtos.CheckoutRequestDTO;
import com.thanh.foodOrder.enums.OrderStatus;
import com.thanh.foodOrder.enums.PaymentStatus;
import com.thanh.foodOrder.enums.TableStatus;
import com.thanh.foodOrder.repository.CartDetailRepository;
import com.thanh.foodOrder.repository.CartRepository;
import com.thanh.foodOrder.repository.OrderDetailRepository;
import com.thanh.foodOrder.repository.OrderRepository;
import com.thanh.foodOrder.util.exception.CommonException;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final BookingTableService bookingTableService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, CartDetailRepository cartDetailRepository,
            OrderDetailRepository orderDetailRepository, BookingTableService bookingTableService,
            ProductService productService) {
        this.orderRepository = orderRepository;
        this.bookingTableService = bookingTableService;
        this.cartDetailRepository = cartDetailRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productService = productService;

    }

    public Order getOrderById(Long id) {

        return this.orderRepository.findById(id).orElseThrow(() -> {
            log.warn("Order with id: {} not found", id);
            return new CommonException("Order with id " + id + " not found");
        });

    }

    // get order on screen admin
    public AdminOrderResponseDTO getResponseOrderById(Long id) {
        Order order = getOrderById(id);
        AdminOrderResponseDTO res = new AdminOrderResponseDTO();
        res.setOrderId(id);
        res.setOrderDate(order.getOrderDate());
        res.setStatus(order.getOrderStatus().name());
        res.setTotalPrice(order.getTotalPrice());
        res.setDiscount(order.getDiscount());
        res.setFinalPrice(order.getFinalPrice());
        res.setTableId(order.getBookingTable().getId());
        res.setPaymentStatus(order.getPaymentStatus());

        return res;

    }

    public OrderResponseDTO getOrderDetail(Long id) {
        Order order = getOrderById(id);
        List<OrderDetail> orderDetails = this.orderDetailRepository.findByOrderId(id);

        OrderResponseDTO res = mapToOrderResponseDTO(order, orderDetails);
        return res;
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
        dto.setTableId(order.getBookingTable().getId());
        dto.setPaymentStatus(order.getPaymentStatus());

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

    private void validBeforePlaceOrder(CheckoutRequestDTO dto, User curUser, List<CartDetail> cartDetails,
            BookingTable bookingTable) {

        if (!this.bookingTableService.checkingTableStatus(bookingTable)) {
            throw new CommonException("This table is busy");

        }
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
        BookingTable bookingTable = this.bookingTableService.getTableById(dto.getTableId());
        // 1. Retrieve CartDetail list by IDs from request
        List<CartDetail> cartDetails = this.cartDetailRepository.findByIdIn(dto.getCartDetailIds());
        validBeforePlaceOrder(dto, curUser, cartDetails, bookingTable);

        // 3. Calculate total price using CartDetail data
        double totalPrice = caculateTotalPrice(cartDetails);

        // 4. Create and save Order
        Order order = new Order();
        order.setUser(curUser);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(totalPrice);
        order.setFinalPrice(totalPrice); // No voucher applied yet
        order.setOrderStatus(OrderStatus.PENDING);
        order.setBookingTable(bookingTable);
        order.setPaymentStatus(PaymentStatus.UNPAID);

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
            Product p = this.productService.getProductById(cd.getProduct().getId());
            p.setQuantity(p.getQuantity() - cd.getQuantity());
            this.productService.saveProduct(p);
        }

        // 6. Remove CartDetail after successful checkout
        this.cartDetailRepository.deleteAll(cartDetails);

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
        bookingTable.setTableStatus(TableStatus.RESERVED);
        // update table status after order
        this.bookingTableService.saveTable(bookingTable);
        OrderResponseDTO res = mapToOrderResponseDTO(order, orderDetails);
        return res;
    }

    @Transactional
    public void payOrder(Long id) {
        Order order = getOrderById(id);

        // 1. Không cho thanh toán lại
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new CommonException("Order already paid");
        }

        // 2. Validate trạng thái order
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new CommonException("Cannot pay cancelled order");
        }

        // 3. Thanh toán thành công
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.COMPLETED);

        // 4. Trả bàn
        BookingTable table = order.getBookingTable();
        table.setTableStatus(TableStatus.AVAILABLE);
    }

    public OrderResponseDTO updateOrder(Order order) {
        Order orderDb = getOrderById(order.getId());

        orderDb.setOrderStatus(order.getOrderStatus());

        this.orderRepository.save(orderDb);

        List<OrderDetail> odDetails = this.orderDetailRepository.findByOrderId(orderDb.getId());

        return mapToOrderResponseDTO(orderDb, odDetails);
    }

}
