package org.africalib.gallery.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.africalib.gallery.backend.entity.Cart;
import org.africalib.gallery.backend.entity.Item;
import org.africalib.gallery.backend.repository.CartRepository;
import org.africalib.gallery.backend.repository.ItemRepository;
import org.africalib.gallery.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CartController {

	@Autowired
	JwtService jwtService;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	ItemRepository itemRepository;

	// 사용자의 장바구니 목록 가져오기
	@GetMapping("/api/cart/items")
	public ResponseEntity getCartItems(@CookieValue(value="token", required=false) String token){

		if(!jwtService.isValid(token)){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		int memberId = jwtService.getId(token);
		List<Cart> carts = cartRepository.findByMemberId(memberId);

		// item의 이름을 출력하기 위한 로직
		List<Integer> itemIds = carts.stream().map(Cart::getItemId).toList();
		List<Item> items = itemRepository.findByIdIn(itemIds);

		return new ResponseEntity<>(items, HttpStatus.OK);

	}

	// 장바구니에 아이템 담기
	@PostMapping("/api/cart/items/{itemId}")
	public ResponseEntity pushCartItem(
			@PathVariable("itemId") int itemId,
			@CookieValue(value="token", required=false) String token
		) {

		if(!jwtService.isValid(token)){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		int memberId = jwtService.getId(token);
		Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

		if(cart == null){
			Cart newCart = new Cart();
			newCart.setMemberId(memberId);
			newCart.setItemId(itemId);
			cartRepository.save(newCart);
		}

		return new ResponseEntity<>(HttpStatus.OK);

	}

	// 장바구니에 아이템 삭제
	@DeleteMapping("/api/cart/items/{itemId}")
	public ResponseEntity removeCartItem(
			@PathVariable("itemId") int itemId,
			@CookieValue(value="token", required=false) String token
		) {

		// 유효한 사용자인지 체크
		if(!jwtService.isValid(token)){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		int memberId = jwtService.getId(token);
		Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

		cartRepository.delete(cart);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
