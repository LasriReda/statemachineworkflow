package com.reda.workflow.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reda.workflow.entity.State;
import com.reda.workflow.entity.StateMachineDefinition;
import com.reda.workflow.repository.StateMachineDefinitionRepository;
import com.reda.workflow.repository.StateRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StateMachineService {

    @Autowired
    private StateMachineFactory<String, String> stateMachineFactory;
    
    @Autowired
    private StateRepository stateRepository;
    
    @Autowired
    private StateMachineDefinitionRepository stateMachineDefinitionRepository;
    
    @Autowired
    private com.reda.workflow.repository.StateMachineRepository stateMachineRepository;
    
    //private final Map<Long, StateMachine<String, String>> stateMachines = new ConcurrentHashMap<>();

    public com.reda.workflow.entity.StateMachine createNewStateMachine(String stateMachineDefName) {
        StateMachine<String, String> stateMachine = stateMachineFactory.getStateMachine(stateMachineDefName);
        startStateMachine(stateMachine);
        com.reda.workflow.entity.StateMachine sm = new com.reda.workflow.entity.StateMachine();
        sm.setName(stateMachineDefName);
        Optional<State> currentState = stateRepository.findById(Long.parseLong(stateMachine.getState().getId()));
        Optional<StateMachineDefinition> definitionOptional = stateMachineDefinitionRepository.findByName(stateMachineDefName);
        sm.setCurrentState(currentState.get());
        sm.setStateMachineDefinition(definitionOptional.get());
        sm = stateMachineRepository.save(sm);
        //stateMachines.put(sm.getId(), stateMachine);
        return sm;
    }

    public void startStateMachine(StateMachine<String, String> stateMachine) {
        //return stateMachine.startReactively();
        stateMachine.start();
    }

    public Flux<StateMachineEventResult<String,String>> sendEvent(StateMachine<String, String> stateMachine, String event) {
        Message<String> message = MessageBuilder.withPayload(event).build();
        return stateMachine.sendEvent(Mono.just(message));
    }

    public String getCurrentState(Long stateMachineId) {
    	StateMachine<String, String> stateMachine = loadAndRestoreWorkflowInstance(stateMachineId); // stateMachines.get(stateMachineId);
    	Optional<State> currentState = stateRepository.findById(Long.parseLong(stateMachine.getState().getId()));
    	return currentState.get().getName();
    }

    public Mono<Object> sendWorkflowEvent(Long stateMachineId, String event){
    	StateMachine<String, String> stateMachine = loadAndRestoreWorkflowInstance(stateMachineId); //stateMachines.get(stateMachineId);
    	Flux<StateMachineEventResult<String, String>> resultFlux = sendEvent(stateMachine, event);
        return resultFlux.collectList()
                .map(results -> {
                    if (!results.isEmpty() && results.stream().allMatch(result -> result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED)) {
                    	com.reda.workflow.entity.StateMachine sm = stateMachineRepository.findById(stateMachineId).get();
                        
                        Optional<State> currentState = stateRepository.findById(Long.parseLong(stateMachine.getState().getId()));
                        Optional<StateMachineDefinition> definitionOptional = stateMachineDefinitionRepository.findByName(sm.getStateMachineDefinition().getName());
                        sm.setCurrentState(currentState.get());
                        sm.setStateMachineDefinition(definitionOptional.get());
                        sm = stateMachineRepository.save(sm);
                        return Mono.just(ResponseEntity.ok("the stateMachine " + stateMachineId + " current state: " + sm.getCurrentState().getName() ));
                    } else {
                    	return Mono.just(ResponseEntity.ok("error processing the event "  ));
                    }
                });
    	
    }
    
    /**
     * Loads a workflow instance from the database and restores its state
     * to a new runtime Spring StateMachine object.
     *
     * @param instanceId The ID of the workflow instance entity to load.
     * @return A new runtime StateMachine object with its state restored.
     */
    @Transactional(readOnly = true)
    public StateMachine<String, String> loadAndRestoreWorkflowInstance(Long instanceId) {
        Optional<com.reda.workflow.entity.StateMachine> workflowInstanceOptional = stateMachineRepository.findById(instanceId);

        if (!workflowInstanceOptional.isPresent()) {
            throw new RuntimeException("Workflow instance with ID " + instanceId + " not found for loading.");
        }
        com.reda.workflow.entity.StateMachine workflowInstance = workflowInstanceOptional.get();

        String definitionName = workflowInstance.getStateMachineDefinition().getName();

        StateMachine<String, String> runtimeStateMachine = stateMachineFactory.getStateMachine(definitionName);

        String currentStateId = workflowInstance.getCurrentState().getId().toString();

        StateMachineContext<String, String> context = new DefaultStateMachineContext<String, String>( currentStateId, null, null, null, null );

        runtimeStateMachine.stop();
        runtimeStateMachine.start();
        runtimeStateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> access.resetStateMachine(context));

        return runtimeStateMachine;
    }
}