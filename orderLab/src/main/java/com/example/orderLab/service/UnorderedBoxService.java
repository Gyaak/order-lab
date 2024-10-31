package com.example.orderLab.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderLab.model.entity.OrderedBox;
import com.example.orderLab.model.entity.OrderedItem;
import com.example.orderLab.model.entity.UnorderedBox;
import com.example.orderLab.model.entity.UnorderedItem;
import com.example.orderLab.model.repository.OrderedItemRepository;
import com.example.orderLab.model.repository.UnorderedBoxRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnorderedBoxService {

	private final  int INTERVAL = 100;
	private final UnorderedBoxRepository boxRepository;
	private final OrderedItemRepository itemRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public void clear() {
		itemRepository.deleteAll();
		boxRepository.deleteAll();
	}

	@Transactional
	public long testMoveWithNaiveQuery(UnorderedBox box, int from, int to) {
		var itemList = getAllItemInBox(box);
		entityManager.clear();
		long startTime = System.nanoTime();
		moveItemWithNaiveQuery(itemList.get(from).getId(), from, to);
		long endTime = System.nanoTime();
		// log.info("testMoveWithNaiveQuery 이동 시간 : {} ms", endTime - startTime);
		return endTime - startTime;
	}

	@Transactional
	public long testMoveWithOneQuery(UnorderedBox box, int from, int to) {
		var itemList = getAllItemInBox(box);
		entityManager.clear();
		long startTime = System.nanoTime();
		moveItemWithOneQuery(box.getId(), itemList.get(from), from, to);
		long endTime = System.nanoTime();
		// log.info("testMoveWithOneQuery 이동 시간 : {} ms", endTime - startTime);
		return endTime - startTime;
	}

	@Transactional
	public long testMoveWithInterval(UnorderedBox box, int from, int to) {
		var itemList = getAllItemInBox(box);
		// entityManager.flush();
		// entityManager.clear();
		long startTime = System.nanoTime();
		moveItemWithIntervalOrder(box, itemList.get(from).getId(), from, to);
		long endTime = System.nanoTime();
		// log.info("testMoveWithInterval 이동 시간 : {} ms", endTime - startTime);
		return endTime - startTime;
	}

	public long testGetAllItemInBox(UnorderedBox box) {
		long startTime = System.nanoTime();
		getAllItemInBox(box);
		long endTime = System.nanoTime();
		// log.info("조회 시간 : {} ms", endTime - startTime);
		return endTime - startTime;
	}

	public List<OrderedItem> getAllItemInBox(UnorderedBox box) {
		return itemRepository.findAllByBoxOrderByItemOrder(box);
	}

	@Transactional
	public UnorderedBox createUnorderedBox(int num) {
		var box = boxRepository.save(UnorderedBox.builder().name("UnorderedBox").build());
		createOrderedItem(box, num);
		return box;
	}

	private void createOrderedItem(UnorderedBox box, int num) {
		for (int i = 0; i < num; i++) {
			itemRepository.save(OrderedItem.builder().itemOrder(i).box(box).build());
		}
	}

	@Transactional
	public UnorderedBox createIntervalUnorderedBox(int num) {
		var box = boxRepository.save(UnorderedBox.builder().name("UnorderedBox").build());
		createIntervalOrderedItem(box, num);
		return box;
	}

	private void createIntervalOrderedItem(UnorderedBox box, int num) {
		for (int i = 0; i < num; i++) {
			itemRepository.save(OrderedItem.builder().itemOrder(i*INTERVAL).box(box).build());
		}
	}

	@Transactional
	public void moveItemWithIntervalOrder(UnorderedBox box, Long targetId, int from, int to) {
		entityManager.clear();
		if(from == to) {
			return;
		}
		var itemList = itemRepository.findAllByBoxOrderByItemOrder(box);
		var target = itemList.get(from);
		if(to <= 0) {
			target.setItemOrder(itemList.get(0).getItemOrder()-INTERVAL);
		} else if(to >= itemList.size()-1) {
			target.setItemOrder(itemList.get(itemList.size()-1).getItemOrder()+INTERVAL);
		} else {
			int leftOrder, rightOrder;
			if(from < to) {
				leftOrder = itemList.get(to).getItemOrder();
				rightOrder = itemList.get(to + 1).getItemOrder();
			} else {
				leftOrder = itemList.get(to - 1).getItemOrder();
				rightOrder = itemList.get(to).getItemOrder();
			}
			if(rightOrder - leftOrder < 2) {
				if(from < to) {
					reorderItems(box.getId(), rightOrder, Integer.MAX_VALUE, INTERVAL);
					target.setItemOrder(leftOrder + INTERVAL / 2);
				} else {
					reorderItems(box.getId(), Integer.MIN_VALUE, leftOrder, -INTERVAL);
					target.setItemOrder(rightOrder - INTERVAL / 2);
				}
				return;
			}
			target.setItemOrder((leftOrder + rightOrder) / 2);
		}
	}

	@Transactional
	protected void reorderItems(Long boxId, int from, int to, int diff) {
		System.out.println("Hello Reorder!");
		itemRepository.updateItemOrder(boxId, from, to, diff);
	}

	@Transactional
	public void moveItemWithNaiveQuery(Long targetId, int curOrder, int destination) {
		var target = itemRepository.findById(targetId).get();
		if(curOrder < destination) {
			updateItemOrder(target.getBox().getId(), curOrder+1, destination, -1);
		} else if(destination < curOrder) {
			updateItemOrder(target.getBox().getId(), destination, curOrder-1, 1);
		}
		target.setItemOrder(destination);
	}

	private void updateItemOrder(Long boxId, int from, int to, int diff) {
		for(var item : itemRepository.findAllByBoxId(boxId)) {
			if(item.getItemOrder()>=from && item.getItemOrder()<=to) {
				item.setItemOrder(item.getItemOrder()+diff);
				itemRepository.save(item);
			}
		}
	}

	@Transactional
	public void moveItemWithOneQuery(Long boxId, OrderedItem target, int curOrder, int destination) {
		if(curOrder == destination) {
			return;
		}
		if(curOrder < destination) {
			itemRepository.updateItemOrder(boxId, curOrder+1, destination, -1);
		} else {
			itemRepository.updateItemOrder(boxId, destination, curOrder-1, 1);
		}
		// target.setItemOrder(destination);
		itemRepository.updateItemOrderByTargetId(target.getId(), destination);
	}
}
