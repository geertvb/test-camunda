package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Slf4j
public class CamundaTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule processEngineRule = TestCoverageProcessEngineRuleBuilder.create().build();

    @Test
    @Deployment(resources = {"testProcess.bpmn"})
    public void shouldExecuteProcess() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");
        RuntimeService runtimeService = processEngineRule.getRuntimeService();

        // Create long string value;
        StringBuffer longStringBuffer = new StringBuffer();
        for (int i = 0; i < 1000; i++) {
            longStringBuffer.append("9876543210");
        }
        String longString = longStringBuffer.toString();

        runtimeService.setVariable(processInstance.getId(), "shortstring", "shortvalue");
        // This fails without long string serializer
        runtimeService.setVariable(processInstance.getId(), "longstring", longString);

        Map<String, Object> variables = runtimeService.getVariables(processInstance.getId());

        String value = "" + variables.get("longstring");
        log.info("longstring: {}", value.substring(0, 20) + "...");
        log.info("shortstring: {}", variables.get("shortstring"));

        complete(task(processInstance));
    }

}
