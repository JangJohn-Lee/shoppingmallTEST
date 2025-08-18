package com.shop.repository;

import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

// 장바구니에 들어갈 상품을  저장하거나 조회하기 위한 리포지토리
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    //카트 아이디와 상품 아이디를 이용해서 상품이 장바구니에 들어있는지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}
