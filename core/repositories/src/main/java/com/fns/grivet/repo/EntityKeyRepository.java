package com.fns.grivet.repo;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.fns.grivet.model.EntityKey;

@Repository
public interface EntityKeyRepository extends PagingAndSortingRepository<EntityKey, UUID> {

}
