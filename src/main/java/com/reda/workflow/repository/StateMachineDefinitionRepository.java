package com.reda.workflow.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reda.workflow.entity.StateMachineDefinition;


public interface StateMachineDefinitionRepository extends JpaRepository<StateMachineDefinition, Long> {
	Optional<StateMachineDefinition> findByName(String name);
}
