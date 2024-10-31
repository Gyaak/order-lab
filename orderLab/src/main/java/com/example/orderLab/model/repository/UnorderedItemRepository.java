package com.example.orderLab.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.orderLab.model.entity.OrderedBox;
import com.example.orderLab.model.entity.UnorderedItem;

@Repository
public interface UnorderedItemRepository extends JpaRepository<UnorderedItem, Long> {
	List<UnorderedItem> findAllByBox(OrderedBox orderedBox);
}
