package test.camunda;

import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class CamundaTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    @Test
    @Deployment(resources = {"testProcess.bpmn"})
    public void shouldExecuteProcess() {
        ProcessInstance processNL = runtimeService().startProcessInstanceByKey("testProcess");
        Task taskNL = task(processNL);
        taskService().setVariableLocal(taskNL.getId(), "country", "NL");

        ProcessInstance processBE = runtimeService().startProcessInstanceByKey("testProcess");
        Task taskBE = task(processBE);
        taskService().setVariableLocal(taskBE.getId(), "country", "BE");

        List<MessageCorrelationResult> correlationResults = runtimeService()
                .createMessageCorrelation("CancelMessage")
                .localVariableEquals("country", "BE")
                .correlateAllWithResult();

        System.out.println("correlationResults = " + correlationResults);

        assertThat(processBE).isEnded();
        assertThat(processNL).isNotEnded();
    }

}
