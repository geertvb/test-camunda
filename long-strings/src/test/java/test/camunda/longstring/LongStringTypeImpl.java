package test.camunda.longstring;

import org.camunda.bpm.engine.variable.impl.type.PrimitiveValueTypeImpl;
import org.camunda.bpm.engine.variable.type.PrimitiveValueType;

import java.util.Map;

public class LongStringTypeImpl extends PrimitiveValueTypeImpl {

    public static PrimitiveValueType LONG_STRING = new LongStringTypeImpl();

    private static final long serialVersionUID = 1L;

    public LongStringTypeImpl() {
        super("LongString", String.class);
    }

    public LongStringValue createValue(Object value, Map<String, Object> valueInfo) {
        return new LongStringValueImpl((String) value, isTransient(valueInfo));
    }

}