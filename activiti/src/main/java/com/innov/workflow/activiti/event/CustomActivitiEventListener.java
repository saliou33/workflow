package com.innov.workflow.activiti.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class CustomActivitiEventListener implements ActivitiEventListener {

    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
            case PROCESS_STARTED:
                log.info("Process Started: {}", event.getProcessInstanceId());
                break;
            case PROCESS_COMPLETED:
                log.info("Process completed: {}", event.getProcessInstanceId());
                break;
            case TASK_COMPLETED:
                log.info("Task completed: {}", event.getExecutionId());
                break;
            case TASK_CREATED:
                log.info("Task created: {}", event.getExecutionId());
                break;
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

}
