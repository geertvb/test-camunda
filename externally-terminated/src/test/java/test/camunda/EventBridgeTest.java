package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessInstanceWithVariablesImpl;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceModificationBuilder;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

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
public class EventBridgeTest {

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
    public void createSupportTask() throws InterruptedException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("Hello", "World!");

        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess", variables);
        String processInstanceId = processInstance.getId();
        Task mainTask = task(processInstance);
        String activityInstanceId = ((ProcessInstanceWithVariablesImpl) processInstance).getExecutionEntity().getActivityInstanceId();

        ProcessInstanceModificationBuilder builder = runtimeService.createProcessInstanceModification(processInstance.getId());
        builder.cancelActivityInstance(activityInstanceId);
        builder.startBeforeActivity("HelloScript");
        builder.cancellationSourceExternal(true);
        builder.execute();

        Thread.sleep(1000L);

        HistoricProcessInstance historicProcessInstance = historyService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        log.info("Process state: {}", historicProcessInstance.getState());

        Thread.sleep(1000L);
    }

}
