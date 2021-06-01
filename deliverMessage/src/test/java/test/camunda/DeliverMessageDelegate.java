package test.camunda;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope("prototype")
@Component
public class DeliverMessageDelegate implements JavaDelegate {

    @Getter
    @Setter
    private Expression messageName;

    @Getter
    @Setter
    private Expression businessKey;

    @Getter
    @Setter
    private Expression processInstanceId;

    @Getter
    @Setter
    private Expression correlationKeys;

    @Getter
    @Setter
    private Expression processVariables;

    protected Object getValue(Expression expression, VariableScope variableScope) {
        if (expression == null) {
            return null;
        }

        return expression.getValue(variableScope);
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("messageName: {}", getValue(messageName, execution));
        log.info("businessKey: {}", getValue(businessKey, execution));
        log.info("processInstanceId: {}", getValue(processInstanceId, execution));
        log.info("correlationKeys: {}", getValue(correlationKeys, execution));
        log.info("processVariables: {}", getValue(processVariables, execution));
    }

}
