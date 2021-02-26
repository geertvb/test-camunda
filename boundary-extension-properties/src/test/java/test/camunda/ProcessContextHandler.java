package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.impl.instance.ProcessImpl;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessContextHandler implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Handling process context");

        DelegateExecution delegateExecution = execution.getProcessInstance();
        FlowElement startElement = delegateExecution.getBpmnModelElementInstance();
        ProcessImpl processElement = (ProcessImpl) startElement.getParentElement();
        ExtensionElements extensionElements = processElement.getExtensionElements();
        Map<String, String> extensionProperties = extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .list()
                .stream()
                .flatMap(properties -> properties.getCamundaProperties().stream())
                .collect(Collectors.toMap(CamundaProperty::getCamundaName, CamundaProperty::getCamundaValue));
        log.info("Extension properties: {}", extensionProperties);
    }
}
