package test.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Collections.emptyMap;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class SupportTaskTest {

    @Rule
    @Autowired
    public ProcessEngineRule processEngineRule;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    public SupportTaskService supportTaskService;

    @Test
    @Deployment(resources = {"mainProcess.bpmn", "supportProcess.bpmn"})
    public void createSupportTask() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("mainProcess");
        Task mainTask = task(processInstance);

        String supportTaskInstanceId = supportTaskService.createSupportTask(mainTask.getId(), "supportX", emptyMap());

    }

}
