package test.camunda.longstring;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.variable.serializer.PrimitiveValueSerializer;
import org.camunda.bpm.engine.impl.variable.serializer.ValueFields;
import org.camunda.bpm.engine.variable.impl.value.UntypedValueImpl;
import org.camunda.bpm.engine.variable.value.TypedValue;

import static java.nio.charset.StandardCharsets.UTF_8;
import static test.camunda.longstring.LongStringTypeImpl.LONG_STRING;

@Slf4j
public class LongStringSerializer extends PrimitiveValueSerializer<LongStringValue> {

    public LongStringSerializer() {
        super(LONG_STRING);
    }

    public LongStringValue convertToTypedValue(UntypedValueImpl untypedValue) {
        return new LongStringValueImpl((String) untypedValue.getValue(), untypedValue.isTransient());
    }

    public LongStringValue readValue(ValueFields valueFields, boolean asTransientValue) {
        return new LongStringValueImpl(new String(valueFields.getByteArrayValue()), asTransientValue);
    }

    public void writeValue(LongStringValue variableValue, ValueFields valueFields) {
        valueFields.setByteArrayValue(variableValue.getValue().getBytes(UTF_8));
    }

    @Override
    protected boolean canWriteValue(TypedValue typedValue) {
        Object value = typedValue.getValue();
        if ((value instanceof String) && ((String) value).length() > 2000) {
            log.info("Long string value detected: {}", ((String) value).substring(0, 20) + "...");
            return true;
        } else {
            return false;
        }
    }

}