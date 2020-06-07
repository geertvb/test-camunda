package test.camunda.event.externalcondition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyEventListener {

    @EventListener(condition = "@eventPredicate.test(#myEvent)")
    public void eventListener(MyEvent myEvent) {
        log.info("Handling: {}", myEvent);
    }

}
