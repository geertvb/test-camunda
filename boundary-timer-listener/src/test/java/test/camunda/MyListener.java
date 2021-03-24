package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyListener implements ExecutionListener {

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected TaskService taskService;

    public void notify(DelegateExecution execution) {
        log.info("notify execution: {}", execution);

        String timerEventId = execution.getCurrentActivityId();

        log.info("timerEventId: {}", timerEventId);

        // get the task this timer event is attached to
        Job timerJob = managementService.createJobQuery().activityId(timerEventId).singleResult();

        log.info("timerJob: {}", timerJob);

        Task task = taskService.createTaskQuery().executionId(timerJob.getExecutionId()).singleResult();

        log.info("task: {}", task);

    }

}
