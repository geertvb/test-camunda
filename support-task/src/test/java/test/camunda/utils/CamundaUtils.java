package test.camunda.utils;

import org.camunda.bpm.engine.task.Task;

public interface CamundaUtils {

    Task waitForTask(String processInstanceId, String taskTypeKey);

}
