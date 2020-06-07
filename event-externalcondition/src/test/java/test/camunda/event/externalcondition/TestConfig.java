package test.camunda.event.externalcondition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = {TestConfig.class})
public class TestConfig {

    protected Set<String> validEvents;

    @Autowired
    public void setValidEvents(@Value("${test.events.validEvents:start,end}") String[] validEvents) {
        this.validEvents = stream(validEvents).collect(toSet());
    }

    @Bean
    public Predicate<MyEvent> eventPredicate() {
        return myEvent -> validEvents.contains(myEvent.getEventName());
    }

}
