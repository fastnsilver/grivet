/*
 * Copyright 2015 - Chris Phillipson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fns.grivet.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.repo.ClassRepository;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

@Component
class SchemaValidator {
    
    private final ClassRepository classRepository;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public SchemaValidator(ClassRepository classRepository, ObjectMapper objectMapper) {
        this.classRepository = classRepository;
        this.objectMapper = objectMapper;
    }

    public ProcessingReport validate(String type, JSONObject payload) throws SchemaValidationException {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            Assert.notNull(type, "Type [%s] does not exist! Schema cannot be retrieved!".formatted(type));
            com.fns.grivet.model.Class c = classRepository.findByName(type);
            Assert.notNull(c, "Persistent model for type [%s], does not exist!".formatted(type));
            JsonNode schemaAsJsonNode = objectMapper.readTree(c.getJsonSchema());
            Assert.notNull(schemaAsJsonNode, "Corrupt persistent Schema representation!");
            final JsonSchema schema = factory.getJsonSchema(schemaAsJsonNode);
            JsonNode instance = objectMapper.readTree(payload.toString());
            Assert.notNull(schema, "Schema not found for type [%s]!".formatted(type));
            Assert.notNull(instance, String.format("Problem generating JsonNode for payload\n\n%s!", payload.toString()));
            return schema.validate(instance);
        } catch (ProcessingException | IOException e) {
            throw new SchemaValidationException("Problem validating [%s] against JSON Schema".formatted(type), e);
        }
    }
    
}
