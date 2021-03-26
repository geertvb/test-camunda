package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskQuery;
import static test.camunda.utils.LoggingUtils.asMultiLine;

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
public class BoundaryEventListenerTest {

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

    protected Task createSupportTask(String assignee, String key) {
        log.info("Send message to create task: {}", key);
        runtimeService.correlateMessage("CreateSupportTask");

        // Get newly created task
        Task supportTask = task(taskQuery()
                .taskDefinitionKey("supportTask")
                .taskUnassigned());

        // Assign user
        taskService.setAssignee(supportTask.getId(), assignee);

        // Set correlation key
        taskService.setVariableLocal(supportTask.getId(), "key", key);

        return supportTask;
    }

    @Test
    @Deployment(resources = {"testProcess.bpmn"})
    public void createSupportTasks() throws Exception {
        log.info("Starting process");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testProcess");

        // Get main task
        Task mainTask = task("mainTask", processInstance);
        log.info("Main task: {}", asMultiLine(mainTask));

        // Create first support task
        Task firstSupportTask = createSupportTask("john", "first");
        log.info("First support task: {}", asMultiLine(firstSupportTask));

        // Create second support task
        Task secondSupportTask = createSupportTask("jane", "second");
        log.info("Second support task: {}", asMultiLine(secondSupportTask));

        // Send message to first task
        log.info("Send message to first task");
        runtimeService.createMessageCorrelation("AutoComplete")
                .localVariableEquals("key", "first")
                .correlate();

        // Just sleep a bit
        Thread.sleep(1000L);
    }

}
