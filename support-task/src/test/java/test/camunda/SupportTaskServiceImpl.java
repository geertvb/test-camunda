package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.camunda.utils.CamundaUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SupportTaskServiceImpl implements SupportTaskService {

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected CamundaUtils camundaUtils;

    protected Map<String, Object> prepareVariables(String mainTaskInstanceId, String supportTaskTypeKey, Map<String, Object> variables) {
        Map<String, Object> newVariables = new HashMap<>();

        // Start with provided variables
        if (variables != null) {
            newVariables.putAll(variables);
        }

        // Add special variables
        newVariables.put("ST:mainTaskInstanceId", mainTaskInstanceId);
        newVariables.put("ST:supportTaskTypeKey", supportTaskTypeKey);

        return newVariables;
    }

    @Override
    public String createSupportTask(String mainTaskInstanceId, String supportTaskTypeKey, Map<String, Object> variables) {
        log.info("Creating support task of type {} for user task {} with variables {}",
                supportTaskTypeKey, mainTaskInstanceId, variables);

        // Prepare process variables
        Map<String, Object> processVariables = prepareVariables(mainTaskInstanceId, supportTaskTypeKey, variables);

        // TODO - Check preconditions

        // Create support task process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("supportProcess", processVariables);

        // Wait for the process to reach the support task
        Task task = camundaUtils.waitForTask(processInstance.getId(), "supportTask");

        log.info("Support task created with id {}", task.getId());
        return task.getId();
    }

    @Override
    public void cancelSupportTask(String supportTaskInstanceId) {
        log.info("Cancelling support task {}", supportTaskInstanceId);

        Task task = camundaUtils.getTask(supportTaskInstanceId);
        runtimeService.messageEventReceived("cancelSupportTask", task.getExecutionId());
    }

}
