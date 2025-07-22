package com.shop.dto;

import com.shop.constant.ItemSellStatus;

// 상품 관리 메뉴 구현하기
public class ItemSearchDto {
    private String searchDateType;  // 시간과 상품 등록일 비교 상품데이터 조회, all : 전체, 1d:최근하루, 1w:최근일주일 등..
    private ItemSellStatus searchSellStatus;    //상품의 판매상태를 기준으로 상품 데이터 조회
    private String searchBy;    // 어떤유형으로 조회할지 ex) itemNm:제품명, createdBy:판매자 아이디
    // 조회할 검색어 저장할 변수 ex)searchBy가 itemNm => 상품명 기준검색, createdBy 상품 등록자 아이디 기준검색
    private String searchQuery = "";
}
