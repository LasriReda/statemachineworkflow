package com.reda.workflow.configuration;

import java.util.List;

public class StateMachineDefinition {
    private List<StateConfig> states;
    private List<TransitionConfig> transitions;

    // Getters and setters
    public List<StateConfig> getStates() {
        return states;
    }

    public void setStates(List<StateConfig> states) {
        this.states = states;
    }

    public List<TransitionConfig> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionConfig> transitions) {
        this.transitions = transitions;
    }
}
