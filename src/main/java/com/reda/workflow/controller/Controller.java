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

    private final Map<String, StateMachine<String, String>> stateMachines = new ConcurrentHashMap<>();

    @PostMapping("/create/{stateMachineId}")
    public Mono<ResponseEntity<String>> createWorkflow(@PathVariable String stateMachineId) {
        if (stateMachines.containsKey(stateMachineId)) {
            return Mono.just(ResponseEntity.badRequest().body("Workflow with ID " + stateMachineId + " already exists."));
        }
        StateMachine<String, String> stateMachine = stateMachineService.createNewStateMachine(stateMachineId);
        stateMachines.put(stateMachineId, stateMachine);
        stateMachineService.startStateMachine(stateMachine);
        return Mono.just(ResponseEntity.ok("Workflow " + stateMachineId + " created and started." ));
    }

    @PostMapping("/{stateMachineId}/event/{event}")
    public Mono<ResponseEntity<String>> sendWorkflowEvent(@PathVariable String stateMachineId, @PathVariable String event) {
        StateMachine<String, String> stateMachine = stateMachines.get(stateMachineId);
        if (stateMachine == null) {
            return Mono.just(ResponseEntity.badRequest().body("Workflow with ID " + stateMachineId + " not found."));
        }
        Flux<StateMachineEventResult<String, String>> resultFlux = stateMachineService.sendEvent(stateMachine, event);
        return resultFlux.collectList()
                .map(results -> {
                    if (!results.isEmpty() && results.stream().allMatch(result -> result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED)) {
                        return ResponseEntity.ok("Event '" + event + "' sent to workflow " + stateMachineId + ". Current state: " + stateMachineService.getCurrentState(stateMachine));
                    } else {
                        return ResponseEntity.badRequest().body("Event '" + event + "' was not fully accepted by workflow " + stateMachineId + ". Current state: " + stateMachineService.getCurrentState(stateMachine));
                    }
                });
    }

    @GetMapping("/{stateMachineId}/status")
    public Mono<ResponseEntity<String>> getWorkflowStatus(@PathVariable String stateMachineId) {
        StateMachine<String, String> stateMachine = stateMachines.get(stateMachineId);
        if (stateMachine == null) {
            return Mono.just(ResponseEntity.badRequest().body("Workflow with ID " + stateMachineId + " not found."));
        }
        if(stateMachine.hasStateMachineError()) {
        	System.out.println("error in stateMachine!");
        }
        return Mono.just(ResponseEntity.ok("Workflow " + stateMachineId + " current state: " + stateMachineService.getCurrentState(stateMachine)));
    }

    @PostMapping("/{stateMachineId}/stop")
    public Mono<ResponseEntity<String>> stopWorkflow(@PathVariable String stateMachineId) {
        StateMachine<String, String> stateMachine = stateMachines.get(stateMachineId);
        if (stateMachine == null) {
            return Mono.just(ResponseEntity.badRequest().body("Workflow with ID " + stateMachineId + " not found."));
        }
        // The service doesn't have a reactive stop, so we call the synchronous one.
        stateMachine.stop();
        return Mono.just(ResponseEntity.ok("Workflow " + stateMachineId + " stopped."));
    }

    @GetMapping("/{stateMachineId}/ended")
    public Mono<ResponseEntity<String>> isWorkflowEnded(@PathVariable String stateMachineId) {
        StateMachine<String, String> stateMachine = stateMachines.get(stateMachineId);
        if (stateMachine == null) {
            return Mono.just(ResponseEntity.badRequest().body("Workflow with ID " + stateMachineId + " not found."));
        }
        return Mono.just(ResponseEntity.ok("" + stateMachineService.hasEnded(stateMachine)));
    }
}