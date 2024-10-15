package test.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.impl.value.ObjectValueImpl;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class CamundaTest {

    private static final Logger log = LoggerFactory.getLogger(CamundaTest.class);
    @Rule
    @ClassRule
    public static ProcessEngineRule processEngineRule = TestCoverageProcessEngineRuleBuilder.create().build();

    @Test
    @Deployment(resources = {"testProcess.bpmn"})
    public void shouldExecuteProcess() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");
        RuntimeService runtimeService = processEngineRule.getRuntimeService();

        // List of strings as variable value
        List<String> myStrings = new ArrayList<>();
        myStrings.add("aaa");
        myStrings.add("bbb");
        myStrings.add("ccc");

        // Store list of strings in variable
        runtimeService.setVariable(processInstance.getId(), "myStrings", myStrings);

        // Retrieve the variable including type information
        TypedValue typedValue = runtimeService.getVariableTyped(processInstance.getId(), "myStrings");

        // Log serialization type and value
        ObjectValueImpl objectValue = (ObjectValueImpl) typedValue;
        log.info("serialization format: {}", objectValue.getSerializationDataFormat());
        log.info("serialization value: {}", objectValue.getValueSerialized());

        // Check untyped
        Object untyped = runtimeService.getVariable(processInstance.getId(), "myStrings");
        log.info("untyped class: {}", untyped.getClass());
        log.info("untyped value: {}", untyped);

        complete(task(processInstance));
    }

}
