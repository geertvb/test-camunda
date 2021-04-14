package test.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.history.event.HistoricExternalTaskLogEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Slf4j
@Component
public class MyListener {

    protected void log(String message, Object value) {
        log.debug("{}: {} - {}", message, value.getClass().getName(), reflectionToString(value, MULTI_LINE_STYLE));
    }

    @Autowired
    protected HistoryService historyService;

    @EventListener
    public void onHistoryEvent(HistoricVariableUpdateEventEntity variable) {
        log("HistoricVariableUpdateEventEntity", variable);

        log.info("Variable for activity instance: {},  {}", variable.getActivityInstanceId(), variable);
    }

    @EventListener
    public void onHistoryEvent(HistoricExternalTaskLogEntity externalTask) {
        log("HistoricExternalTaskLogEntity", externalTask);

        List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                .activityInstanceIdIn(externalTask.getActivityInstanceId())
                .list();

        log.info("Variables for activity instance: {}, {}", externalTask.getActivityInstanceId(), variables);
    }

}
