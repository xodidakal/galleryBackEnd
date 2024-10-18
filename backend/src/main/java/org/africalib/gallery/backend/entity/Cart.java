package org.africalib.gallery.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="carts")
public class Cart {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // 자동증가 값이다를 선언
	private int id;

	@Column
	private int memberId;

	@Column
	private int itemId;
}
