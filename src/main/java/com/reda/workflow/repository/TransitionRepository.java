package com.reda.workflow.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reda.workflow.entity.Transition;


public interface TransitionRepository extends JpaRepository<Transition, Long> {
	List<Transition> findByStateMachineDefinitionId(Long stateMachineDefinitionId);
}
