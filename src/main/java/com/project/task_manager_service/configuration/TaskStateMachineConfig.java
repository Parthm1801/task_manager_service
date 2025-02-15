package com.project.task_manager_service.configuration;

import com.project.task_manager_service.enums.TaskEvent;
import com.project.task_manager_service.enums.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class TaskStateMachineConfig extends StateMachineConfigurerAdapter<TaskStatus, TaskEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<TaskStatus, TaskEvent> states) throws Exception {
        states.withStates()
                .initial(TaskStatus.PENDING)
                .state(TaskStatus.IN_PROGRESS)
                .state(TaskStatus.COMPLETED)
                .state(TaskStatus.OVERDUE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TaskStatus, TaskEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(TaskStatus.PENDING)
                    .target(TaskStatus.IN_PROGRESS)
                    .event(TaskEvent.START)
                    .action(ctx -> log.info("State change from pending to in-progress"))
                .and()
                .withExternal()
                    .source(TaskStatus.IN_PROGRESS)
                    .target(TaskStatus.COMPLETED)
                    .event(TaskEvent.COMPLETE)
                    .action(ctx -> log.info("State change from in-progress to completed"))
                .and()
                .withExternal()
                    .source(TaskStatus.PENDING)
                    .target(TaskStatus.OVERDUE)
                    .event(TaskEvent.MARK_OVERDUE)
                    .action(ctx -> log.info("State change from pending to overdue"));
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<TaskStatus, TaskEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(new StateMachineListenerAdapter<TaskStatus, TaskEvent>() {
                    @Override
                    public void stateChanged(State<TaskStatus, TaskEvent> from, State<TaskStatus, TaskEvent> to) {
                        System.out.println("State changed from " + (from != null ? from.getId() : "None") + " to " + to.getId());
                    }
                });
    }
}
