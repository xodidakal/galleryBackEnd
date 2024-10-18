package org.africalib.gallery.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name="members")
public class Member {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // 자동증가 값이다를 선언
	private int id;

	@Column(length=50, nullable=false, unique = true)
	private String email;

	@Column(length=100, nullable=false)
	private String password;

	@Column(length=50)
	private String name;

	@Column(length=50)
	private String phoneNumber;
}
