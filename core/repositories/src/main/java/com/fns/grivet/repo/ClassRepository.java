package com.fns.grivet.repo;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fns.grivet.model.Class;

@Repository
public interface ClassRepository extends PagingAndSortingRepository<Class, UUID> {

    public Class findByName(@Param("name") String name);
}
