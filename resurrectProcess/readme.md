# Restart with original id

See StartProcessInstanceAtActivitiesCmd

```java
    ExecutionEntity processInstance = processDefinition
        .createProcessInstance(instantiationBuilder.getBusinessKey(), instantiationBuilder.getCaseInstanceId(), initialActivity);
```

## TODO

1. Capture old process instance id

Use for example threadlocal variable

2. Set id before newExecution.insert();

```java
  protected static ExecutionEntity createNewExecution() {
    ExecutionEntity newExecution = new ExecutionEntity();
    initializeAssociations(newExecution);
    newExecution.insert();

    return newExecution;
  }
```

3. Skip insert of historic process instance

! Check if it doesn't have side effects

```java
  @Override
  public void fireHistoricProcessStartEvent() {
    ProcessEngineConfigurationImpl configuration = Context.getProcessEngineConfiguration();
    HistoryLevel historyLevel = configuration.getHistoryLevel();
    // TODO: This smells bad, as the rest of the history is done via the
    // ParseListener
    if (historyLevel.isHistoryEventProduced(HistoryEventTypes.PROCESS_INSTANCE_START, processInstance)) {

      HistoryEventProcessor.processHistoryEvents(new HistoryEventProcessor.HistoryEventCreator() {
        @Override
        public HistoryEvent createHistoryEvent(HistoryEventProducer producer) {
          return producer.createProcessInstanceStartEvt(processInstance);
        }
      });
    }
  }
```

4. Remove end date from historic process instance