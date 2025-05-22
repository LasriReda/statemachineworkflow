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
@Table(name = "TRANSITION")
public class Transition {
	
	@Id
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "source_id")
	private State source;
	
	@ManyToOne
	@JoinColumn(name = "target_id")
	private State target;
	
	@Column(name = "event")
	private String event;

	@ManyToOne
    @JoinColumn(name="machine_id", nullable=false)
	private StateMachineDefinition stateMachineDefinition;
}
