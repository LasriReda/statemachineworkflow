package com.reda.workflow.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "STATE_MACHINE")
public class StateMachine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="machine_id", nullable=false)
	private StateMachineDefinition stateMachineDefinition;
    
    @Column(name = "name")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "current_state_id")
    private State currentState;
    
    @Column(name = "assignee")
    private String assignee;
}