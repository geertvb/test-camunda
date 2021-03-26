package test.camunda.utils;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class LoggingUtils {

    public static String asMultiLine(Object execution) {
        return reflectionToString(execution, MULTI_LINE_STYLE);
    }

}
