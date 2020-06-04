package test.camunda;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackageClasses = TestConfig.class)
@ImportResource("classpath:springTypicalUsageTest-context.xml")
public class TestConfig {
}
