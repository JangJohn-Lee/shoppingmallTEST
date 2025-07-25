package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    /* 상품조회 조건을 담은 itemSearchDto객체와 페이징 정보를 담고 있는 pageable 객체를 파라미터로 받는 getAdminItemPage 메소드를 정의.
    반환데이터로 Page<Item> 객체로 반환함 */
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    //메인페이지에 보여줄 상품리스트 가져오기
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
