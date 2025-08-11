package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
// 상품 주문에서 웹 페이지의 새로고침 없이 서버에  주문을 요청하기 위해서 비동기 방식 사용
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    //스프링에서 비동기 처리할때 @RequestBody와 @ResponseBody 어노테이션 사용
    //@RequestBody:HTTP 요청의 본문 body에 담긴 내용을 자바 객체로 전달
    //@ResponseBody:자바 객체를 HTTP요청의 Body로 전달
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bingResult, Principal principal) {

        //주문 정보를 받는 orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사
        if (bingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);   //에러정보를 ResponseEntity 객체에 담아서 반환
        }

        String email = principal.getName();
        Long orderId;
        try {
            orderId = orderService.order(orderDto, email);  //화면으로부터 넘어오는 주문 정보와 회원의 이메일 정보를 이용하여 주문 로직을 호출
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);    //결과값으로 생성된 주문 번호와 요청이 성공했다는 HTTP  응답 상태 코드를 반환합니다.
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        //자바스크립트에서 취소할 주문 번호는 조작이 가능하므로 다른 사람의 주문을 취소하지 못하도록 주문 취소 권한 검사
        if (!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        //주문 취소 로직 호출
        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
