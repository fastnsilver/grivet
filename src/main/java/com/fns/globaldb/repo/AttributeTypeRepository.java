package com.fns.globaldb.repo;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.fns.globaldb.model.AttributeType;

@Repository
public class AttributeTypeRepository {

    private static Map<String, AttributeType> nameMap = Arrays.stream(AttributeType.values()).collect(Collectors.toMap(k -> k.getType(), k -> k));
    private static Map<Integer, AttributeType> idMap = Arrays.stream(AttributeType.values()).collect(Collectors.toMap(k -> k.getId(), k -> k));
    
    public AttributeType findByType(String type) {
        return nameMap.get(type);
    }
    
    public AttributeType findOne(Integer id) {
        return idMap.get(id);
    }
    
}
