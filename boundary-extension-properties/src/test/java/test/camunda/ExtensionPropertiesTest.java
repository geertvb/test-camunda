package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static java.lang.Thread.sleep;
import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
        // ...other parameters...
        classes = {TestApp.class},
        properties = {
                "camunda.bpm.generate-unique-process-engine-name=true",
                // this is only needed if a SpringBootProcessApplication
                // is used for the test
                "camunda.bpm.generate-unique-process-application-name=true",
                "spring.datasource.generate-unique-name=true",
                // additional properties...
                "camunda.bpm.eventing.execution=true",
                "camunda.bpm.eventing.history=true",
                "camunda.bpm.eventing.task=true"
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class ExtensionPropertiesTest {

    @Autowired
    public ProcessEngine processEngine;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;

    @Before
    public void setUp() {
        init(processEngine);
    }

    @Test
    @Deployment(resources = {"testProcess.bpmn"})
    public void createSupportTask() throws Exception {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");
        Task mainTask = task(processInstance);
        //taskService.complete(mainTask.getId());
        sleep(10000L);
    }

}
