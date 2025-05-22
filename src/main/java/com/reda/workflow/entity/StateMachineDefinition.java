package com.reda.workflow.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "STATE_MACHINE_DEFINITION")
public class StateMachineDefinition {
	@Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "name")
    private String name;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="stateMachineDefinition")
	private List<State> states;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="stateMachineDefinition")
    private List<Transition> transitions;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="stateMachineDefinition")
	private List<StateMachine> stateMachines;

}
