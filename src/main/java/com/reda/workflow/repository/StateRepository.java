package com.reda.workflow.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reda.workflow.entity.State;


public interface StateRepository extends JpaRepository<State, Long> {
	List<State> findByStateMachineDefinitionId(Long string);
}
