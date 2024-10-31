package com.example.orderLab.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.orderLab.model.entity.UnorderedBox;

@Repository
public interface UnorderedBoxRepository extends JpaRepository<UnorderedBox, Long> {
}
