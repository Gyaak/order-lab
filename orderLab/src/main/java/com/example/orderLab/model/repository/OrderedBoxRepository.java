package com.example.orderLab.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.orderLab.model.entity.OrderedBox;

@Repository
public interface OrderedBoxRepository extends JpaRepository<OrderedBox, Long> {
}
