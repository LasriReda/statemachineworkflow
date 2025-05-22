package com.reda.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reda.workflow.entity.StateMachine;


public interface StateMachineRepository extends JpaRepository<StateMachine, Long> {
}
