package test.camunda;

import java.util.Map;

public interface SupportTaskService {

    String createSupportTask(String mainTaskInstanceId, String supportTaskTypeKey, Map<String, Object> variables);

}
