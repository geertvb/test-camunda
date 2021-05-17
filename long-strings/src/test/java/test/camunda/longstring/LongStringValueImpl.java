package test.camunda.longstring;

import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl;

import static test.camunda.longstring.LongStringTypeImpl.LONG_STRING;

public class LongStringValueImpl extends PrimitiveTypeValueImpl<String> implements LongStringValue {

    private static final long serialVersionUID = 1L;

    public LongStringValueImpl(String value) {
        super(value, LONG_STRING);
    }

    public LongStringValueImpl(String value, boolean isTransient) {
        this(value);
        this.isTransient = isTransient;
    }
}