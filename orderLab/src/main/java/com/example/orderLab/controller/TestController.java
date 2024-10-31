package com.example.orderLab.controller;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderLab.model.entity.OrderedItem;
import com.example.orderLab.model.repository.OrderedBoxRepository;
import com.example.orderLab.service.OrderedBoxService;
import com.example.orderLab.service.UnorderedBoxService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

	private final int itrNum = 300;
	private final int num = 100;
	private final long MOD = 1_000_000;

	private final OrderedBoxRepository orderedBoxRepository;
	private final OrderedBoxService orderedBoxService;
	private final UnorderedBoxService unorderedBoxService;
	private final TransactionTemplate transactionTemplate;

	@GetMapping("/test")
	public String test() {

		var orderedBox = orderedBoxService.createOrderedBox(num);
		var unorderedBox1 = unorderedBoxService.createUnorderedBox(num);
		var unorderedBox2 = unorderedBoxService.createUnorderedBox(num);
		var intervalBox = unorderedBoxService.createIntervalUnorderedBox(num);

		long a = 0;
		long b = 0;
		long c = 0;
		long d = 0;
		long e = 0;
		long f = 0;
		long g = 0;
		long h = 0;
		for(int i = 0; i<itrNum; i++) {
			int from = (int)(Math.random() * num) % num;
			int to = (int)(Math.random() * num) % num;

			a += orderedBoxService.testMoveOrder(orderedBox, from, to);
			b += unorderedBoxService.testMoveWithOneQuery(unorderedBox1, from, to);
			c += unorderedBoxService.testMoveWithNaiveQuery(unorderedBox2, from, to);
			g += unorderedBoxService.testMoveWithInterval(intervalBox, from, to);
			// System.out.println(unorderedBoxService.getAllItemInBox(intervalBox).stream().map(OrderedItem::getId).toList());
		}

		for(int i = 0; i<itrNum; i++) {
			d += orderedBoxService.testGetAllItemInBox(orderedBox);
			e += unorderedBoxService.testGetAllItemInBox(unorderedBox1);
			// f += unorderedBoxService.testGetAllItemInBox(unorderedBox2);
			// h += unorderedBoxService.testGetAllItemInBox(intervalBox);
		}


		return String.format("%d %d %d %d %d %d %d %d", a/MOD, b/MOD, c/MOD, g/MOD, d/MOD, e/MOD, f/MOD, h/MOD);
	}


}
