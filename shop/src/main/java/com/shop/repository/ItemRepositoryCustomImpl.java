package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

//ItemRepositoryCustom 상송
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    //동적 쿼리 생성 위해 JPAQueryFactory 클래스 사용
    private JPAQueryFactory queryFactory;
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /*상품 판매 상태 조건이 전체(null)일 경우 null 리턴, 결과값 Null 일때 where 에서 해당 조건 무시.
    상품 판매 조건이 null 이 아니라 판매중 or 품절일때 해당조건의 상품만 조회*/
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        }else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        }else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    /*searchBy값에 따라 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어 포함한 상품을
    조회 하도록 조건값 반환*/
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        }else if(StringUtils.equals("createBy", searchBy)){
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        /**
         * queryFactory를 사용해서 쿼리 생성
         * selectFrom(QItem.item): 상품 데이터를 조회하기 위해 QItem의 item 지정
         * where 조건절 : BooleanExpression 반환하는 조건문. ',' 단위로 넣으면 and로 인식
         * offset : 데이터를 가지고 올 시작 인덱스 정하기
         * limit : 한번에 가지고 올 최대 개수 지정
         * fetchResult() : 조회한 리스트 및 전체 개수를 포함하는 QueryResults를 반환, 여기서는
         상품 데이터 리스트 조회 및 상품 데이터 전체 개수를 조회하는 2번의 쿼리문 실행.
         **/
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content = results.getResults();
        long total = results.getTotal();
        // 조회한 데이터를 page클래스의 구현체 pageImpl 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }
}
