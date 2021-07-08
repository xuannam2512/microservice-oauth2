package com.microservice.oauth2.config.oauth2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("status", value.getHttpErrorCode());
        jsonGenerator.writeObjectField("message", value.getMessage());
        jsonGenerator.writeObjectField(value.getOAuth2ErrorCode(), value.getMessage());
        if (value.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                jsonGenerator.writeStringField(key, add);
            }
        }
        jsonGenerator.writeEndObject();
    }
}
