package com.reda.workflow.controller;

import com.reda.workflow.service.StateMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/workflow")
public class Controller {

    @Autowired
    private StateMachineService stateMachineService;

    

    @PostMapping("/create/{stateMachineDefId}")
    public Mono<ResponseEntity<String>> createWorkflow(@PathVariable String stateMachineDefName) {

        com.reda.workflow.entity.StateMachine stateMachine = stateMachineService.createNewStateMachine(stateMachineDefName);
        
        return Mono.just(ResponseEntity.ok("Workflow " + stateMachineDefName + " created and started with ID: " + stateMachine.getId() ));
    }

    @PostMapping("/{stateMachineId}/event/{event}")
    public Mono<Object> sendWorkflowEvent(@PathVariable Long stateMachineId, @PathVariable String event) {
        
        return stateMachineService.sendWorkflowEvent(stateMachineId, event);
        
    }

    @GetMapping("/{stateMachineId}/status")
    public Mono<ResponseEntity<String>> getWorkflowStatus(@PathVariable Long stateMachineId) {
    	String state = stateMachineService.getCurrentState(stateMachineId);
    	
        return Mono.just(ResponseEntity.ok("Workflow " + stateMachineId + " current state: " + state));
    }

}