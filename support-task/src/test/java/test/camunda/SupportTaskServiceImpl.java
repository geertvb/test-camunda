package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.camunda.utils.CamundaUtils;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
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
        newVariables.put("mainTaskInstanceId", mainTaskInstanceId);
        newVariables.put("supportTaskTypeKey", supportTaskTypeKey);

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
    public void cancelSupportTask(String mainTaskInstanceId, String supportTaskTypeKey, Map<String, Object> variables) {

        String messageName="cancelSupportTask_"+mainTaskInstanceId+"_"+supportTaskTypeKey;

        // correlate the message by its unique name
        try {
            runtimeService.correlateMessage(messageName);
            log.info("Support task cancelled with message name '{}'", messageName);
        } catch (MismatchingMessageCorrelationException e) {
            e.printStackTrace();
            log.info("Support task cancellation exception {}", e.getMessage());

        }
    }

}
