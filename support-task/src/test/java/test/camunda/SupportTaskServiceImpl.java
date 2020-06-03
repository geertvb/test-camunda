package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import test.camunda.utils.CamundaUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SupportTaskServiceImpl implements SupportTaskService {

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected CamundaUtils camundaUtils;

    @Override
    public String createSupportTask(String mainTaskInstanceId, String supportTaskTypeKey, Map<String, Object> variables) {
        log.info("Creating support task of type {} for user task {} with variables {}",
                supportTaskTypeKey, mainTaskInstanceId, variables);

        // Prepare the process variables
        Map<String, Object> newVariables = new HashMap<>();
        if (variables != null) {
            newVariables.putAll(variables);
        }
        newVariables.put("ST:mainTaskInstanceId", mainTaskInstanceId);
        newVariables.put("ST:supportTaskTypeKey", supportTaskTypeKey);

        // TODO - Check preconditions

        // Create support task process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "supportProcess",
                newVariables);

        // Wait for the process to reach the support task
        Task task = camundaUtils.waitForTask(processInstance.getId(), "supportTask");

        log.info("Support task created with id {}", task.getId());
        return task.getId();
    }

}
