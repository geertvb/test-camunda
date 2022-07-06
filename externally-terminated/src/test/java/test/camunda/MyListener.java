package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Slf4j
@Component
public class MyListener {

    protected void log(String message, Object value) {
        log.debug("{}: {} - {}", message, value.getClass().getName(), reflectionToString(value, MULTI_LINE_STYLE));
    }

    @EventListener
    public void onDelegateTask(DelegateTask delegateTask) {
        log("onDelegateTask", delegateTask);
    }

    @EventListener
    public void onTaskEvent(TaskEvent taskEvent) {
        log("onTaskEvent", taskEvent);
    }

    @EventListener
    public void onDelegateExecution(DelegateExecution delegateExecution) {
        log("onDelegateExecution", delegateExecution);
    }

    @EventListener
    public void onExecutionEvent(ExecutionEvent executionEvent) {
        log("onExecutionEvent", executionEvent);
    }

    @EventListener
    public void onHistoryEvent(HistoryEvent historyEvent) {
        log("onHistoryEvent", historyEvent);
    }

}
