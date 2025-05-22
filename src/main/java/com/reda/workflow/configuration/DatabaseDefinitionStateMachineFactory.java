package com.reda.workflow.configuration;

import com.reda.workflow.entity.State;
import com.reda.workflow.entity.StateMachineDefinition;
import com.reda.workflow.entity.Transition;
import com.reda.workflow.repository.StateMachineDefinitionRepository;
import com.reda.workflow.repository.StateRepository;
import com.reda.workflow.repository.TransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.stereotype.Component; // Use @Component or @Service

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID; // Import UUID


@Component
public class DatabaseDefinitionStateMachineFactory implements StateMachineFactory<String, String> {

    private final StateMachineDefinitionRepository stateMachineDefinitionRepository;
    private final StateRepository stateRepository;
    private final TransitionRepository transitionRepository;

    public DatabaseDefinitionStateMachineFactory(
            StateMachineDefinitionRepository stateMachineDefinitionRepository,
            StateRepository stateRepository,
            TransitionRepository transitionRepository) {
        this.stateMachineDefinitionRepository = stateMachineDefinitionRepository;
        this.stateRepository = stateRepository;
        this.transitionRepository = transitionRepository;
    }

    /**
     * Creates a new StateMachine instance based on a definition identified by its name.
     * This method fulfills the StateMachineFactory interface contract.
     *
     * @param machineId The name of the StateMachineDefinition to build.
     * @return A new configured StateMachine instance.
     * @throws IllegalArgumentException if the definition is not found.
     */
    @Override
    public StateMachine<String, String> getStateMachine(String machineDefName) {
    	System.out.println("==> Building state machine " );
    	// use the machineId string to find the definition by name
        Optional<StateMachineDefinition> definitionOptional = stateMachineDefinitionRepository.findByName(machineDefName);

        if (!definitionOptional.isPresent()) {
            throw new IllegalArgumentException("State machine definition with name '" + machineDefName + "' not found.");
        }

        StateMachineDefinition definition = definitionOptional.get();

        // Fetch associated States and Transitions
        List<State> states = stateRepository.findByStateMachineDefinitionId(definition.getId());
        List<Transition> transitions = transitionRepository.findByStateMachineDefinitionId(definition.getId());

         if (states.isEmpty()) {
             throw new IllegalArgumentException("No states found for state machine definition with name '" + machineDefName + "'");
        }


        // Use StateMachineBuilder to build the state machine
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

        try {
            // Configure States
        	StateConfigurer<String, String> stateConfigurer = builder.configureStates().withStates();

            String initialStateId = null;
            Set<String> endStateIds = new HashSet<>();

            for (State state : states) {
                String stateId = state.getId().toString();
                stateConfigurer.state(stateId);

                if (state.isInitialState()) { 
                    if (initialStateId != null) {
                        throw new IllegalStateException("Multiple initial states defined for machine " + definition.getName());
                    }
                    initialStateId = stateId;
                    stateConfigurer.initial(stateId);
                }
                if (state.isEndState()) {
                    endStateIds.add(stateId);
                    stateConfigurer.end(stateId);
                }
            }

            if (initialStateId == null) {
                throw new IllegalStateException("No initial state defined for machine " + definition.getName());
            }

            // Configure Transitions
            //StateMachineBuilder.Builder<String, String>.TransitionConfigurer transitionConfigurer = builder.configureTransitions();
            StateMachineTransitionConfigurer<String, String> transitionConfigurer = builder.configureTransitions();

            for (Transition transition : transitions) {
                if (transition.getSource() != null && transition.getTarget() != null) {
                    transitionConfigurer
                            .withExternal()
                            .source(transition.getSource().getId().toString())
                            .target(transition.getTarget().getId().toString())
                            .event(transition.getEvent());
                } else {
                    System.out.println("Skipping transition with missing source or target state for machine " + definition.getName() + ", transition ID: " + transition.getId());
                }
            }

            // Build and return the StateMachine instance
            // The machineId parameter from the factory method can be used as the runtime machine ID
             StateMachine<String, String> stateMachine = builder.build();
             return stateMachine;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build state machine for definition name '" + machineDefName + "'", e);
        }
    }


    @Override
    public StateMachine<String, String> getStateMachine() {
        throw new UnsupportedOperationException("Creating a state machine without a definition ID/name is not supported by this factory. Use getStateMachine(String machineId) with the definition name.");
    }

	@Override
	public StateMachine<String, String> getStateMachine(UUID uuid) {
		return null;
	}

}
