package test.camunda.utils;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamundaUtilsImpl implements CamundaUtils {

    @Autowired
    public TaskService taskService;

    public Task waitForTask(String processInstanceId, String taskTypeKey) {
        // TODO - Use awaitility to wait
        return taskService.createTaskQuery()
                .taskDefinitionKey(taskTypeKey)
                .singleResult();
    }

}
