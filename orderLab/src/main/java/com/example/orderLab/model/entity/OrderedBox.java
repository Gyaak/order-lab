package com.example.orderLab.model.entity;

import java.util.List;

import com.example.orderLab.model.OrderListConverter;
import com.fasterxml.jackson.databind.annotation.EnumNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderedBox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column
	@Setter
	@Convert(converter = OrderListConverter.class)
	private List<Long> itemOrderList; // box에 속한 item들의 순서를 List로 제어

	public void moveItem(Long targetId, int destination) {
		itemOrderList.remove(targetId);
		itemOrderList.add(destination, targetId);
	}
}
