package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    //주문이력을 조회하는 쿼리
    //JPQL로 사용되는 구문. + 이전에 띄워쓰기 하지않으면 붙여쓴걸로 인식되니까 꼭 주의하기
    @Query("select o from Order o " +
    "where o.member.email = :email " +
    "order by o.orderDate desc"
    )
    //현재 로그인 한 사용자의 주문데이터를 페이징 조건에 맞춰서 조회
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    @Query("select count(o) from Order o " +
    " where o.member.email = :email"
    )
    //현재 로그인한 회원의 주문 개수가 몇 개 인지 조회
    Long countOrder(@Param("email") String email);
}