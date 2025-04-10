package com.reda.workflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StateMachineService {

    @Autowired
    private StateMachineFactory<String, String> stateMachineFactory;

    public StateMachine<String, String> createNewStateMachine(String machineId) {
        StateMachine<String, String> stateMachine = stateMachineFactory.getStateMachine(machineId);
        return stateMachine;
    }

    public void startStateMachine(StateMachine<String, String> stateMachine) {
        //return stateMachine.startReactively();
        stateMachine.start();
    }

    public Flux<StateMachineEventResult<String,String>> sendEvent(StateMachine<String, String> stateMachine, String event) {
        Message<String> message = MessageBuilder.withPayload(event).build();
        return stateMachine.sendEvent(Mono.just(message));
    }

    public String getCurrentState(StateMachine<String, String> stateMachine) {
        return stateMachine.getState().getId();
    }

    public boolean hasEnded(StateMachine<String, String> stateMachine) {
        return stateMachine.isComplete();
    }
}