package test.camunda;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import java.util.Collection;
import java.util.Objects;

public class CamundaPropertiesUtils {

    public static Collection<CamundaProperty> getCamundaProperties(BpmnModelInstance bpmnModelInstance, String elementId) {
        BaseElement modelElementInstance = bpmnModelInstance.getModelElementById(elementId);
        ExtensionElements extensionElements = modelElementInstance.getExtensionElements();
        return extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .singleResult()
                .getCamundaProperties();
    }

    public static String getCamundaPropertyValue(Collection<CamundaProperty> camundaProperties, String name) {
        for (CamundaProperty camundaProperty : camundaProperties) {
            if (Objects.equals(name, camundaProperty.getCamundaName())) {
                // TODO - Evaluate expression
                return camundaProperty.getCamundaValue();
            }
        }
        return null;
    }

    // TODO - Maybe a threadlocal is faster and cleaner
    // TODO - Maybe also use the task instance id for safety

    protected static void contextPush(VariableScope variableScope, String name, String value) {
        variableScope.setVariableLocal("CTX:" + name, value);
    }

    protected static String contextPop(VariableScope variableScope, String name) {
        String cancelledBy = (String) variableScope.getVariable("CTX:" + name);
        if (StringUtils.isNotBlank(cancelledBy)) {
            variableScope.removeVariable("CTX:" + name);
        }
        return cancelledBy;
    }

}
