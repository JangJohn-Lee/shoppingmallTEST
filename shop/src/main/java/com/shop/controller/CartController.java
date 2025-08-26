package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

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

    //장바구니 상품의 수량을 업데이트 하는 요청 처리하는  메소드
    @PatchMapping(value = "/cartItem/{cartItemId}")     //PATCH는 요청된 자원의 일부를  업데이트 할 때 PATCH사용, 장바구니 상품의 수량만 업데이트하니까 @PatchMapping 사용
    public @ResponseBody ResponseEntity updateCartItem (@PathVariable("cartItemId") Long cartItemId, int count, Principal principal) {
        if(count <= 0){     //장바구니에 담겨있는 상품의 개수를 0개 이하로 업데이트 요청을 할때 에러 메시지를 담아서 반환.
            return new ResponseEntity<String>("최소 1개 이상 담아주세요.",HttpStatus.BAD_REQUEST);
        }else if(!cartService.validateCartItem(cartItemId, principal.getName())){       //수정권한 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.updateCartItemCount(cartItemId, count);     //장바구니 상품개수 업데이트
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    //장바구니 상품을 삭제하는 요청을 처리하는 로직
    @DeleteMapping(value = "/cartItem/{cartItemId}")        //DELETE 메소드는 요청된 자원을 삭제할때 사용한다. 장바구니 상품 삭제할때 DeleteMapping 사용하기
    public @ResponseBody ResponseEntity deleteCartItem (@PathVariable("cartItemId") Long cartItemId, Principal principal) {
        if(!cartService.validateCartItem(cartItemId, principal.getName())){     //수정 권한 체크하기
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);     //해당 장바구니 상품 삭제
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

//    @PostMapping(value = "/cart/orders")
//    public @ResponseBody ResponseEntity orderCartItem(@ResponseBody CartOrderDto cartOrderDto, Principal principal){
//
//    }
}
