package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static test.camunda.utils.LoggingUtils.asMultiLine;

@Slf4j
@Component
public class AutoCompleteListener implements ExecutionListener {

    @Autowired
    protected RuntimeService runtimeService;

    public void notify(DelegateExecution execution) {
        log.info("AutoCompleteListener execution: {}", asMultiLine(execution));

        String currentActivityId = execution.getCurrentActivityId(); // AutoCompleteEvent

        List<EventSubscription> eventSubscriptions = runtimeService.createEventSubscriptionQuery()
                .activityId(currentActivityId)
                .processInstanceId(execution.getProcessInstanceId())
                .list();

        for (EventSubscription eventSubscription : eventSubscriptions) {
            log.info("eventSubscription: {}", asMultiLine(eventSubscription));

            ExecutionEntity subscriptionExecution = (ExecutionEntity) runtimeService.createExecutionQuery()
                    .executionId(eventSubscription.getExecutionId())
                    .singleResult();

            log.info("subscriptionExecution: {}", asMultiLine(subscriptionExecution));
        }
    }

}
