package com.fns.grivet.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fns.grivet.query.NamedQuery;

@RepositoryRestResource(collectionResourceRel = "namedQueries", path = "api/v1/namedQueries")
public interface NamedQueryRepository extends PagingAndSortingRepository<NamedQuery, Integer> {

    public NamedQuery findByName(@Param("name") String name);
}
