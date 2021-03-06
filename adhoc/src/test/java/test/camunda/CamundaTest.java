package test.camunda;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

public class CamundaTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    @Test
    @Deployment(resources = {"parentProcess.bpmn", "childProcess.bpmn"})
    public void shouldExecuteProcess() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("parentProcess");
//        complete(task(processInstance));
        runtimeService().messageEventReceived("StartChild", processInstance.getId());
    }

}
