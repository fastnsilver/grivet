package com.fns.grivet.service;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.repo.ClassRepository;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

@Component
class SchemaValidator {
    
    private ClassRepository classRepository;
    private ObjectMapper objectMapper;
    
    @Autowired
    public SchemaValidator(ClassRepository classRepository, ObjectMapper objectMapper) {
        this.classRepository = classRepository;
        this.objectMapper = objectMapper;
    }

    public ProcessingReport validate(String type, JSONObject payload) throws SchemaValidationException {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            com.fns.grivet.model.Class c = classRepository.findByName(type);
            Assert.notNull(type, String.format("Type [%s] does exist! Schema cannot be retrieved!", type));
            JsonNode schemaAsJsonNode = objectMapper.readTree(c.getJsonSchema());
            final JsonSchema schema = factory.getJsonSchema(schemaAsJsonNode);
            JsonNode instance = objectMapper.readTree(payload.toString());
            Assert.notNull(schema, String.format("Schema not found for type [%s]!", type));
            Assert.notNull(instance, String.format("Problem generating JsonNode for payload\n\n%s!", payload.toString()));
            return schema.validate(instance);
        } catch (ProcessingException | IOException e) {
            throw new SchemaValidationException(String.format("Problem validating [%s] against JSON Schema", type), e);
        }
    }
    
}
