package test.camunda;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class CamundaTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    @Test
    @Deployment(resources = {"testProcess.bpmn"})
    public void shouldExecuteProcess() throws Exception {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");
        System.out.println("processInstance = " + processInstance.getId());

        ProcessInstance oldProcessInstance = runtimeService().createProcessInstanceQuery()
                .processDefinitionKey("testProcess")
                .singleResult();
        System.out.println("processInstance = " + oldProcessInstance.getId());

        String processInstanceId = processInstance.getProcessInstanceId();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        Task oldTask = task(processInstance);
        System.out.println("oldTask = " + oldTask);
        complete(oldTask);

        runtimeService().restartProcessInstances(processDefinitionId)
                .startBeforeActivity("UserTask_1")
                .processInstanceIds(processInstanceId)
                .execute();

        ProcessInstance newProcessInstance = runtimeService().createProcessInstanceQuery()
                .processDefinitionKey("testProcess")
                .singleResult();
        System.out.println("processInstance = " + newProcessInstance.getId());

        Task newTask = task(newProcessInstance);
        System.out.println("newTask = " + newTask);
    }

}
