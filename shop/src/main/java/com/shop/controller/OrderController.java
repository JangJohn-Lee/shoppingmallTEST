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

    @PostMapping(value="/order")
    //스프링에서 비동기 처리할때 @RequestBody와 @ResponseBody 어노테이션 사용
    //@RequestBody:HTTP 요청의 본문 body에 담긴 내용을 자바 객체로 전달
    //@ResponseBody:자바 객체를 HTTP요청의 Body로 전달
    public @ResponseBody ResponseEntity order (@RequestBody @Valid OrderDto orderDto, BindingResult bingResult, Principal principal){

        //주문 정보를 받는 orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사
        if(bingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);   //에러정보를 ResponseEntity 객체에 담아서 반환
        }

        String email = principal.getName();
        Long orderId;
        try{
            orderId = orderService.order(orderDto, email);  //화면으로부터 넘어오는 주문 정보와 회원의 이메일 정보를 이용하여 주문 로직을 호출
        }catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);    //결과값으로 생성된 주문 번호와 요청이 성공했다는 HTTP  응답 상태 코드를 반환합니다.
    }

    //구매이력을 조회할 수 있도록 로직 호출하는 메소드
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHis(@PathVariable("page") Optional<Integer> page,
                           Principal principal, Model model){
        //한번에 가지고 올 주문의 개수는 4개로 설정하기
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,4);

        //현재 로그인한 회원은 이메일과 페이징 객체를 파라미터로 전달해서 화면에 전달한 주문 목록데이터를 리턴받음
        Page<OrderHistDto> orderHistDtoList =
                orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", orderHistDtoList.getNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

}