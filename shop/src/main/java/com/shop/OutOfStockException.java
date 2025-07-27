package com.shop;

public class OutOfStockException extends RuntimeException {
    //주문 수량보다 재고가 많으면 발생시킬 exception
    public OutOfStockException(String message) {
        super(message);
    }
}
