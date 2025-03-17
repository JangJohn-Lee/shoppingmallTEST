package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemNm(String itemNm);     //상품명으로 Item에서 검색

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);  //상품명 or 상품 상세설명으로 조회 하는 쿼리
    
    List<Item> findByPriceLessThan(Integer price);  //price 변수보다 값이 작은 상품 데이터 조회

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);  //OrderBy + 속성명 + DESC 키워드를 사용해서 상품 가격이 높은 순으로 데이터를 "내림차순"으로 조회

}
