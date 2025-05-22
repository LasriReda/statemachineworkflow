package com.reda.workflow.configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reda.workflow.entity.StateMachineDefinition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;

import java.io.IOException;
import java.util.List;

//@Configuration
public class JsonStateMachineFactoryConfig {

    //@Value("classpath:statemachine.json")
    //private Resource stateMachineResource;
/*
    @Bean
    public StateMachineBuilder.Builder<String, String> stateMachineBuilder() throws Exception {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            StateMachineDefinition definition = mapper.readValue(stateMachineResource.getInputStream(), StateMachineDefinition.class);

            StateConfigurer<String, String> stateConfigurer = builder.configureStates().withStates();
            Long initialState = null;
            Long endState = null;
            List<State> states = definition.getStates();

            for (State stateConfig : states) {
                stateConfigurer.state(stateConfig.getId().toString());
                if ("INITIAL".equals(stateConfig.getType())) {
                	initialState = stateConfig.getId();
                    stateConfigurer.initial(stateConfig.getId().toString());
                } else if ("END".equals(stateConfig.getType())) {
                	endState = stateConfig.getId();
                    stateConfigurer.end(stateConfig.getId().toString());
                }
            }

            if (initialState == null && !states.isEmpty()) {
                throw new IllegalStateException("No initial state defined.");
            }

            if (endState == null && !states.isEmpty()) {
                throw new IllegalStateException("No final state defined.");
            }

            // Configure Transitions
            StateMachineTransitionConfigurer<String, String> transitions = builder.configureTransitions();
            for (Transition transitionConfig : definition.getTransitions()) {
                transitions
                        .withExternal()
                        .source(transitionConfig.getSource())
                        .target(transitionConfig.getTarget())
                        .event(transitionConfig.getEvent());
            }

            return builder;

        } catch (IOException e) {
            throw new RuntimeException("Error reading state machine configuration from JSON", e);
        }
    }

    
    @Bean
    public StateMachineFactory<String, String> stateMachineFactory(
            StateMachineBuilder.Builder<String, String> builder) throws Exception {
        return builder.createFactory();//.build();
    }
    */
}