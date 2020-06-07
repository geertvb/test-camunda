package test.camunda.event.externalcondition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ExternalConditionTest {

    @Autowired
    public ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void testEventListener() {
        applicationEventPublisher.publishEvent(MyEvent.builder().eventName("start").build());
        applicationEventPublisher.publishEvent(MyEvent.builder().eventName("claim").build());
        applicationEventPublisher.publishEvent(MyEvent.builder().eventName("complete").build());
        applicationEventPublisher.publishEvent(MyEvent.builder().eventName("end").build());
    }

}
