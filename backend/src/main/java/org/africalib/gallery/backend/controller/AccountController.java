package org.africalib.gallery.backend.controller;

import java.util.Map;

import org.africalib.gallery.backend.entity.Member;
import org.africalib.gallery.backend.repository.MemberRepository;
import org.africalib.gallery.backend.service.JwtService;
import org.africalib.gallery.backend.service.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	JwtService jwtService;

	@PostMapping("/api/account/login")
	public ResponseEntity login(@RequestBody Map<String, String> params,
								HttpServletResponse res){
		Member member = memberRepository.findByEmailAndPassword(params.get("email"), params.get("password"));

		if(member != null) {
			// id 값을 토큰화
			int id = member.getId();
			String token = jwtService.getToken("id", id);

			// 보안상 서버 상에서 쿠키에 저장하는 것이 안전하기 때문에
			// 프론트엔드에서 쿠키로 저장하는 것이 아닌 백엔드에서 쿠키에 저장한다.
			Cookie cookie = new Cookie("token", token);
			cookie.setHttpOnly(true); // 자바스크립트로는 접근 불가
			cookie.setPath("/");

			res.addCookie(cookie);

			return new ResponseEntity<>(id, HttpStatus.OK);
		}

		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/api/account/logout")
	public ResponseEntity logout(HttpServletResponse res){
		Cookie cookie = new Cookie("token", null);
		cookie.setPath("/");
		cookie.setMaxAge(0);

		res.addCookie(cookie);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@GetMapping("/api/account/check")
	public ResponseEntity check(@CookieValue(value="token", required=false) String token){
		Claims claims = jwtService.getClaims(token);

		if (claims != null) {
			int id = Integer.parseInt(claims.get("id").toString());
			return new ResponseEntity<>(id, HttpStatus.OK);
		}

		return new ResponseEntity<>(null, HttpStatus.OK);
	}

}
