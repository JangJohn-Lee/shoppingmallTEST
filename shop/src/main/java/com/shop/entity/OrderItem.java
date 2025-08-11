package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문수량

    private int count;      //수량

//    private LocalDateTime regTime;  //등록시간
//
//    private LocalDateTime updateTime;   //수정시간

    //주문할 상품과 주문 수량을 통해 OrderItem 객체 생성
    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);  //주문할 상품과 주문 수량 세팅
        orderItem.setOrderPrice(item.getPrice());

        item.removeStock(count);    //주문 수량만큼 재고수량 감소
        return orderItem;
    }

    //주문 가격과 주문 수량을 곱해서 주문한 총 가격을 계산
    public int getTotalPrice() {
        return orderPrice * count;
    }

    //주문 취소시 주문 수량만큼 상품의 재고를 더해주기
    public void cancel(){
        this.getItem().addStock(count);
    }
}
