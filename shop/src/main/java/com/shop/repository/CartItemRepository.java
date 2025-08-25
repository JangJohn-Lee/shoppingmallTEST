package com.shop.repository;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// 장바구니에 들어갈 상품을  저장하거나 조회하기 위한 리포지토리
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //카트 아이디와 상품 아이디를 이용해서 상품이 장바구니에 들어있는지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    // 장바구니 페이지에 전달할 CartDetailDto 리스트를 쿼리 하나로 조회 하는 JPQL문
    /* CartDetailDto의 생성자를 이용해서 DTO 반환 할때는 해당 코드처럼 new 키워드와 해당 DTO의 패키지, 클래스명을 적어줘야함.
    생성자 파라미터 순서는 DTO 클래스에 명시한 순서대로 작성. */
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
    "from CartItem ci, ItemImg im " +
    "join ci.item i " +
    "where ci.cart.id = :cartId " +
    "and im.item.id = ci.item.id " +
    "and im.repimgYn = 'Y' " +  // 장바구니에 담겨있는 상품의 대표 이미지만 가지고 오도록 조건문 작성
    "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
