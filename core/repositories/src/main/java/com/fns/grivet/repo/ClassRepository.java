package com.fns.grivet.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fns.grivet.model.Class;

@RepositoryRestResource(collectionResourceRel = "classes", path = "api/v1/classes")
public interface ClassRepository extends PagingAndSortingRepository<Class, Integer> {

    public Class findByName(@Param("name") String name);
}
