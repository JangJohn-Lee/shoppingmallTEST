package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderHistDto {

    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));  //OrderHistDto 클래스의 생성자로 order 객체를 파라미터로 받아서 멤버 변수값 세팅. 주문날짜는 포맷수정
        this.orderStatus = order.getOrderStatus();
    }

    private Long orderId;   //주문아이디
    private String orderDate;   //주문날짜
    private OrderStatus orderStatus; //주문상태
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();    //주문 상품 리스트
    
    //orderItemDto 객체를 주문 상품 리스트에 추가
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }
}
