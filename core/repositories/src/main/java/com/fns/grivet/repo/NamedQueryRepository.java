package com.fns.grivet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fns.grivet.query.NamedQuery;

@RepositoryRestResource(collectionResourceRel = "namedQueries", path = "namedQueries")
public interface NamedQueryRepository extends JpaRepository<NamedQuery, Integer> {

	public NamedQuery findByName(@Param("name") String name);

}
