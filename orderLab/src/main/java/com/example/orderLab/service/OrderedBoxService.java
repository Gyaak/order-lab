package com.example.orderLab.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderLab.model.entity.OrderedBox;
import com.example.orderLab.model.entity.UnorderedItem;
import com.example.orderLab.model.repository.OrderedBoxRepository;
import com.example.orderLab.model.repository.UnorderedItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderedBoxService {

	private final OrderedBoxRepository boxRepository;
	private final UnorderedItemRepository itemRepository;

	public void clear() {
		boxRepository.deleteAll();
		itemRepository.deleteAll();
	}
	@Transactional
	public OrderedBox createOrderedBox(int num) {
		var box = boxRepository.save(OrderedBox.builder()
			.name("OrderedBox")
			.build());
		box.setItemOrderList(getItemIdList(box, num));
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
			itemList.add(
				itemRepository.save(
					UnorderedItem.builder()
						.content("UnorderedItem"+(i+1))
						.box(box)
						.build()
				)
			);
		}
		return itemList.stream()
			.map(UnorderedItem::getId)
			.toList();
	}


}
