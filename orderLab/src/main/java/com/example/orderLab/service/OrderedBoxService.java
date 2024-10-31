package com.example.orderLab.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderLab.model.entity.OrderedBox;
import com.example.orderLab.model.entity.UnorderedBox;
import com.example.orderLab.model.entity.UnorderedItem;
import com.example.orderLab.model.repository.OrderedBoxRepository;
import com.example.orderLab.model.repository.UnorderedItemRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderedBoxService {

	private final OrderedBoxRepository boxRepository;
	private final UnorderedItemRepository itemRepository;

	// num개의 item을 생성하고 from번째 아이템을 to번째로 이동
	@Transactional
	public long testMoveOrder(OrderedBox box, int from, int to) {
		Long targetId = box.getItemOrderList().get(from);
		long startTime = System.nanoTime();
		if(from != to) {
			box.moveItem(targetId, to);
			boxRepository.save(box);
		}
		long endTime = System.nanoTime();
		// log.info("testMoveOrder 이동 시간 : {} ms", endTime - startTime);
		return endTime - startTime;
	}

	public long testGetAllItemInBox(OrderedBox box) {
		long startTime = System.nanoTime();
		getAllItemInBox(box);
		long endTime = System.nanoTime();
		// log.info("조회 시간 : {} ms", endTime - startTime);
		return endTime - startTime;
	}

	public void clear() {
		itemRepository.deleteAll();
		boxRepository.deleteAll();
	}

	@Transactional
	public OrderedBox createOrderedBox(int num) {
		var box = boxRepository.save(OrderedBox.builder().name("OrderedBox").build());
		box.setItemOrderList(getItemIdList(box, num));
		return box;
	}

	@Transactional
	public OrderedBox moveItem(Long boxId, Long targetId, int curOrder, int destination) {
		var box = boxRepository.findById(boxId).get();
		box.moveItem(targetId, destination);
		return box;
	}

	public List<UnorderedItem> getAllItemInBox(OrderedBox box) {
		return itemRepository.findAllByBox(box).stream()
			.sorted(Comparator.comparing(item -> box.getItemOrderList().indexOf(item.getId())))
			.toList();
	}


	private List<Long> getItemIdList(OrderedBox box, int num) {
		List<UnorderedItem> itemList = new ArrayList<>();
		for(int i = 0; i < num; i++) {
			itemList.add(itemRepository.save(UnorderedItem.builder().content("UnorderedItem"+(i+1)).box(box).build()));
		}
		return itemList.stream()
			.map(UnorderedItem::getId)
			.toList();
	}


}
