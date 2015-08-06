package com.fns.globaldb.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fns.globaldb.model.Class;

@Repository
public interface ClassRepository extends PagingAndSortingRepository<Class, Integer> {

    public Class findByName(@Param("name") String name);
}
