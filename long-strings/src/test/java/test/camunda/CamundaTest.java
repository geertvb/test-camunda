package test.camunda;

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

        // This fails
        // runtimeService.setVariable(processInstance.getId(), "longstring", longString);

        // This doesn't fail
        runtimeService.setVariable(processInstance.getId(), "longstring", new LongString(longString));

        Map<String, Object> variables = runtimeService.getVariables(processInstance.getId());

        complete(task(processInstance));
    }

    public static class LongString {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public LongString() {
        }

        public LongString(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
