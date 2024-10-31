package com.example.orderLab.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Order 컬럼을 가지고 있지 않은 Item
 * 순서를 item이 속한 박스에서 제어
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnorderedItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	private OrderedBox box;
}
