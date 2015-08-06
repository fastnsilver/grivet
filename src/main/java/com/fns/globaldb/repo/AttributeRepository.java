package com.fns.globaldb.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fns.globaldb.model.Attribute;

@RepositoryRestResource
public interface AttributeRepository extends PagingAndSortingRepository<Attribute, Integer> {

    public Attribute findByName(@Param("name") String name);
    
}
