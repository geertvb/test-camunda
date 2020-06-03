package test.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import test.camunda.utils.CamundaUtils;
import test.camunda.utils.CamundaUtilsImpl;

import static java.util.Collections.emptyMap;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

// TODO - Use Spring-Based Testing.
// See https://docs.camunda.org/manual/latest/user-guide/spring-framework-integration/testing/
public class SupportTaskTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    protected RuntimeService runtimeService = runtimeService();
    protected TaskService taskService = taskService();
    protected CamundaUtils camundaUtils = camundaUtils(taskService);
    protected SupportTaskService supportTaskService = supportTaskService(runtimeService, camundaUtils);

    public CamundaUtils camundaUtils(TaskService taskService) {
        CamundaUtilsImpl camundaUtils = new CamundaUtilsImpl();
        camundaUtils.taskService = taskService;
        return camundaUtils;
    }

    public SupportTaskService supportTaskService(RuntimeService runtimeService, CamundaUtils camundaUtils) {
        SupportTaskServiceImpl supportTaskService = new SupportTaskServiceImpl();
        supportTaskService.runtimeService = runtimeService;
        supportTaskService.camundaUtils = camundaUtils;
        return supportTaskService;
    }

    @Test
    @Deployment(resources = {"mainProcess.bpmn", "supportProcess.bpmn"})
    public void createSupportTask() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("mainProcess");
        Task mainTask = task(processInstance);

        String supportTaskInstanceId = supportTaskService.createSupportTask(mainTask.getId(), "supportX", emptyMap());

    }

}
