package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static test.camunda.CamundaPropertiesUtils.*;

@Slf4j
@Component
public class MyListener {

    public static final String VAR_CANCELLED_BY = "CANCELLED_BY";
    public static final String VAR_MIMIC_OUTCOME = "MIMIC_OUTCOME";
    public static final String VAR_OUTCOME = "OUTCOME";

    public static final String EVENT_DELETE = "delete"; // TODO - Use Camunda constant

    @EventListener
    public void onDelegateTask(DelegateTask delegateTask) {
        if (EVENT_DELETE.equals(delegateTask.getEventName())) {
            log.info("Task deleted");

            // Retrieving the "cancelled by" information via context (execution variable)
            String cancelledBy = contextPop(delegateTask, VAR_CANCELLED_BY);
            if (StringUtils.isNotBlank(cancelledBy)) {
                log.info("Task cancelled by: {}", cancelledBy);

                Collection<CamundaProperty> camundaProperties = getCamundaProperties(delegateTask.getBpmnModelInstance(), cancelledBy);
                String mimicOutcome = getCamundaPropertyValue(camundaProperties, VAR_MIMIC_OUTCOME);

                if (StringUtils.isNotBlank(mimicOutcome)) {
                    log.info("Mimic complete with outcome: {}", mimicOutcome);
                    delegateTask.setVariableLocal(VAR_OUTCOME, mimicOutcome);
                }
            }
        }
    }

    @EventListener
    public void onDelegateExecution(DelegateExecution delegateExecution) {
        String deleteReason = ((ExecutionEntity) delegateExecution).getDeleteReason();

        if (deleteReason != null
                && deleteReason.startsWith("Cancel scope activity Activity(")
                && deleteReason.endsWith(") executed.")) {

            String activityKey = StringUtils.substringBetween(deleteReason,
                    "Cancel scope activity Activity(", ") executed.");
            log.info("Cancelled by: {}", activityKey);

            // Todo - Only pass the information if it concerns an interrupting boundary event on a user task.

            // Passing the "cancelled by" information via context (execution variable)
            contextPush(delegateExecution, VAR_CANCELLED_BY, activityKey);
        }

    }

}
