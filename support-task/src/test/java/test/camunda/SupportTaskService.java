package test.camunda;

import java.util.Map;

public interface SupportTaskService {

    /**
     * Creates a support task for the given main task.
     * @param mainTaskInstanceId task instance id of the main task
     * @param supportTaskTypeKey task type of the support task to be created
     * @param variables
     * @return task instance id of the created support task
     */
    String createSupportTask(String mainTaskInstanceId, String supportTaskTypeKey, Map<String, Object> variables);

    /**
     * Cancels a support task
     * @param supportTaskInstanceId task instance id of the support task to cancel
     */
    void cancelSupportTask(String supportTaskInstanceId);

}
