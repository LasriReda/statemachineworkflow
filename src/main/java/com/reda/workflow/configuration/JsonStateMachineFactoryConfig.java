package com.reda.workflow.configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;

import java.io.IOException;
import java.util.List;

@Configuration
public class JsonStateMachineFactoryConfig {

    @Value("classpath:statemachine.json")
    private Resource stateMachineResource;

    @Bean
    public StateMachineBuilder.Builder<String, String> stateMachineBuilder() throws Exception {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            StateMachineDefinition definition = mapper.readValue(stateMachineResource.getInputStream(), StateMachineDefinition.class);

            StateConfigurer<String, String> stateConfigurer = builder.configureStates().withStates();
            String initialState = null;
            String endState = null;
            List<StateConfig> states = definition.getStates();

            for (StateConfig stateConfig : states) {
                stateConfigurer.state(stateConfig.getId());
                if ("INITIAL".equals(stateConfig.getType())) {
                	initialState = stateConfig.getId();
                    stateConfigurer.initial(stateConfig.getId());
                } else if ("END".equals(stateConfig.getType())) {
                	endState = stateConfig.getId();
                    stateConfigurer.end(stateConfig.getId());
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
            for (TransitionConfig transitionConfig : definition.getTransitions()) {
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
    public org.springframework.statemachine.config.StateMachineFactory<String, String> stateMachineFactory(
            StateMachineBuilder.Builder<String, String> builder) throws Exception {
        return builder.createFactory();//.build();
    }
    
}