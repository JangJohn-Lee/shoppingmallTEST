package com.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

//메인페이지에서 상품 보여줄 때 쓸 dto
@Getter
@Setter
public class MainItemDto {
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    // QueryProjection 어노테이션을 생성자에 선언해서 QueryDsl로 조회시 MainItemDto 객체로 바로 받아오기
    @QueryProjection
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
