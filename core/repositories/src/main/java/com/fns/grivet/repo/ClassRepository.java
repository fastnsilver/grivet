package com.fns.grivet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fns.grivet.model.Class;

@RepositoryRestResource(collectionResourceRel = "classes", path = "classes")
public interface ClassRepository extends JpaRepository<Class, Integer> {

    public Class findByName(@Param("name") String name);
}
