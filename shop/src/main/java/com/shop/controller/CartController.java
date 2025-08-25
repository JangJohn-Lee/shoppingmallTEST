package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
//장바구니 요청 관련 컨트롤러
public class CartController {
    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {
        //장바구니에 담을 상품 정보를 받는 cartItemDto객체에 데이터 바인딩 할때 에러검사
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = principal.getName(); //현재 로그인한 회원의 이메일 정보를 변수에 저장
        Long cartItemId;
        try {
            cartItemId = cartService.addCart(cartItemDto, email);   //화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일 정보를 이용하여 장바구니에 상품을 담는 로직을 호출
        } catch(Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK); //결과값으로 생성된 장바구니 상품아이디와 요청이 성공하였다는 HTTP 응답 상태 코드를 반환.
    }

    @GetMapping(value="/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto>  cartDetailList = cartService.getCartList(principal.getName());  // 현재 로그인 한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품정보 조회
        model.addAttribute("cartItems",cartDetailList);     //조회한 장바구니 상품 정보를 뷰로 전달
        return "cart/cartList";
    }
}
