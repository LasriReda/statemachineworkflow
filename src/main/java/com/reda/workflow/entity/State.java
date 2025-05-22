package com.reda.workflow.entity;


import java.io.Serializable;

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
@Table(name = "STATE")
public class State {


	@Id
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "is_initial")
	private boolean initialState;
	
	@Column(name = "is_end_state")
	private boolean endState;
	
	@ManyToOne
    @JoinColumn(name="machine_id", nullable=false)
	private StateMachineDefinition stateMachineDefinition;
	
	@Column(name = "default_roles")
	private String defaultRoles;
	
}
