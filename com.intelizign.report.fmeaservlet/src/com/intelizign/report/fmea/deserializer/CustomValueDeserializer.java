package com.intelizign.report.fmea.deserializer;
 
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonDeserializer;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 
public class CustomValueDeserializer extends JsonDeserializer<List<Object>> {
 
    @Override
    public List<Object> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        List<Object> values = new ArrayList<>();
        // Check if the node is an array
        if (node.isArray()) {
            for (JsonNode item : node) {
                values.add(item.asText());
            }
        } else {
            // Single object, so just add it to the list
            values.add(node.asText());
        }
        return values;
    }
}