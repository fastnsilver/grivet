package com.fns.grivet.repo;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fns.grivet.query.NamedQuery;

@Repository
public interface NamedQueryRepository extends PagingAndSortingRepository<NamedQuery, UUID> {

    public NamedQuery findByName(@Param("name") String name);
}
