package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceModificationBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.historyService;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {TestApp.class},
        properties = {
                "camunda.bpm.generate-unique-process-engine-name=true",
                "camunda.bpm.generate-unique-process-application-name=true",
                "spring.datasource.generate-unique-name=true",
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
    protected HistoryService historyService;

    @Before
    public void setUp() {
        init(processEngine);
    }


    public void modify(Consumer<ProcessInstanceModificationBuilder> builderConsumer) throws InterruptedException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("Hello", "World!");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ExternallyTerminatedProcess", variables);
        String processInstanceId = processInstance.getId();

        Thread.sleep(1000L);

        // Prepare the modification builder
        ProcessInstanceModificationBuilder builder = runtimeService.createProcessInstanceModification(processInstanceId);
        builder.cancellationSourceExternal(true);

        // build the actual modification instructions
        builderConsumer.accept(builder);

        // Execute the modifications
        builder.execute();

        Thread.sleep(1000L);

        String state = historyService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getState();

        log.info("Process state: {}", state);
        assertEquals("COMPLETED", state);
    }

    @Test
    @DirtiesContext
    @Deployment(resources = {"testProcess.bpmn"})
    public void completed() throws Exception {
        modify(builder -> {
            // Cancel current activity = MainTask
            builder.cancelAllForActivity("MainTask");

            // Start after MainTask
            builder.startAfterActivity("MainTask");
        });
    }

    @Test
    @DirtiesContext
    @Deployment(resources = {"testProcess.bpmn"})
    public void interrupted() throws Exception {
        modify(builder -> {
            // Start after MainTask
            builder.startAfterActivity("MainTask");

            // Cancel current activity = MainTask
            builder.cancelAllForActivity("MainTask");
        });
    }

    @Test
    @DirtiesContext
    @Deployment(resources = {"testProcess.bpmn"})
    public void externallyTerminated() throws Exception {
        modify(builder -> {
            String ancestorActivityInstanceId = historyService.createHistoricActivityInstanceQuery()
                    .activityId("EmbeddedProcess")
                    .singleResult()
                    .getId();

            log.info("Ancestor activity: {}", ancestorActivityInstanceId);

            // Start after MainTask
            builder.startAfterActivity("MainTask", ancestorActivityInstanceId);

            String activityInstanceId = historyService.createHistoricActivityInstanceQuery()
                    .activityId("MainTask")
                    .singleResult()
                    .getId();

            log.info("Activity: {}", activityInstanceId);

            // Cancel current activity = MainTask
            builder.cancelActivityInstance(activityInstanceId);
        });
    }

}
