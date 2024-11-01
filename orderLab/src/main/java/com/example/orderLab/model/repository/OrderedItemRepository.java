package com.example.orderLab.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.orderLab.model.entity.OrderedBox;
import com.example.orderLab.model.entity.OrderedItem;
import com.example.orderLab.model.entity.UnorderedBox;

@Repository
public interface OrderedItemRepository extends JpaRepository<OrderedItem, Long> {

	List<OrderedItem> findAllByBoxOrderByItemOrder(UnorderedBox box);
	List<OrderedItem> findAllByBoxIdOrderByItemOrder(Long boxId);
	List<OrderedItem> findAllByBoxId(Long boxId);

	void deleteByBox(UnorderedBox box);

	@Modifying
	@Query(value = "UPDATE OrderedItem item SET item.itemOrder = item.itemOrder + :diff WHERE item.box.id = :boxId AND item.itemOrder BETWEEN :fromOrder AND :toOrder")
	void updateItemOrder(@Param("boxId") Long boxId, @Param("fromOrder") int fromOrder, @Param("toOrder") int toOrder, @Param("diff") int diff);

	@Modifying
	@Query(value = "UPDATE OrderedItem  item SET item.itemOrder = :newOrder WHERE item.id = :targetId")
	void updateItemOrderByTargetId(@Param("targetId") Long targetId, @Param("newOrder") int newOrder);
}
