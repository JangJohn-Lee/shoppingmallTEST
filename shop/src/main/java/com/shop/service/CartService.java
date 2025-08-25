package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
//장바구니에 상품 담는 로직
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberService memberService;


    public Long addCart(CartItemDto cartItemDto, String email) {
        //장바구니에 담을 상품 엔티티 조회
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        //현재 로그인 한 회원 엔티티 조회
        Member member = memberRepository.findByEmail(email);

        //현재 로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());

        //상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티 생성
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        //현재 상품이 장바구니에 이미 들어가 있는지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), cartItemDto.getItemId());

        //현재 상품이 장바구니에 이미 들어가 있는지 조회.
        if (savedCartItem != null) {

            //장바구니에 이미 있던 상품일 경우 기존 수량에 현재 장바구니에 담을 수량만큼 더해주기.
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();

        } else {
            // 장바구니 엔티티, 상품 엔티티, 장바구니에 담을 수량을 이용하여 CartItem 엔티티 생성
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());

            //장바구니에 들어갈 상품을 저장
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    //현재 로그인한 회원의 정보를 이용해서 장바구니에 들어있는 상품 조회하는 메소드
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());      //현재 로그인한 회원의 장바구니 엔티티 조회하기
        if (cart == null) {     //장바구니에 상품을 한번도 안담으면 엔티티가 없어서 빈리스트 반환
            return cartDetailDtoList;
        }

        //장바구니에 담겨잇는 상품 정보 조회
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    // 장바구니 상품이 현재 로그인한 사용자의 것인지 확인하는 메소드
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long CartItemId, String email) {
        // 현재 로그인한 회원 조회
        Member curMember = memberRepository.findByEmail(email);

        // 파라미터로 넘어온 cartItemId로 장바구니 상품 조회
        CartItem cartItem = cartItemRepository.findById(CartItemId)
                .orElseThrow(EntityNotFoundException::new);

        // 해당 장바구니 상품이 속한 회원(장바구니 주인) 조회
        Member savedMember = cartItem.getCart().getMember();

        // 현재 로그인한 회원과 장바구니 소유 회원의 이메일이 같은지 비교
        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;   // 다르면 본인 장바구니 아님 → false
        }

        return true;        // 같으면 본인 장바구니 → true
    }

    // 장바구니 상품의 수량을 변경하는 메소드
    public void updateCartItemCount(Long cartItemId, int count) {
        // 파라미터로 넘어온 cartItemId로 장바구니 상품 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        // 장바구니 상품 수량 업데이트
        cartItem.updateCount(count);
    }
}