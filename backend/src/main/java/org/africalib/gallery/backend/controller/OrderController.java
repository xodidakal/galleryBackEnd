package org.africalib.gallery.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.africalib.gallery.backend.dto.OrderDto;
import org.africalib.gallery.backend.entity.Cart;
import org.africalib.gallery.backend.entity.Item;
import org.africalib.gallery.backend.entity.Order;
import org.africalib.gallery.backend.repository.CartRepository;
import org.africalib.gallery.backend.repository.ItemRepository;
import org.africalib.gallery.backend.repository.OrderRepository;
import org.africalib.gallery.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class OrderController {

	@Autowired
	JwtService jwtService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	CartRepository cartRepository;

	// 주문내역 보기
	@GetMapping("/api/orders")
	public ResponseEntity getOrder(
			@CookieValue(value="token", required=false) String token)
	{
		if(!jwtService.isValid(token)){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		int memberId = jwtService.getId(token);
		List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);

		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	// 결제하기
	@Transactional
	@PostMapping("/api/orders")
	public ResponseEntity pushOrder(
			@RequestBody OrderDto dto,
			@CookieValue(value="token", required=false) String token)
	{

		if(!jwtService.isValid(token)){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		Order newOrder = new Order();
		int memberId = jwtService.getId(token);

		newOrder.setMemberId(memberId);
		newOrder.setName(dto.getName());
		newOrder.setAddress(dto.getAddress());
		newOrder.setPayment(dto.getPayment());
		newOrder.setCardNumber(dto.getCardNumber());
		newOrder.setItems(dto.getItems());

		orderRepository.save(newOrder);
		cartRepository.deleteByMemberId(memberId);

		return new ResponseEntity<>(HttpStatus.OK);

	}
}
