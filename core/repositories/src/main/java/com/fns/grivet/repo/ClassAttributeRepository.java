package com.fns.grivet.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fns.grivet.model.ClassAttribute;

@Repository
public interface ClassAttributeRepository extends PagingAndSortingRepository<ClassAttribute, UUID> {

    public List<ClassAttribute> findByCid(@Param("cid") UUID cid);
    
    public ClassAttribute findByCidAndAid(@Param("cid") UUID cid, @Param("aid") UUID aid);
}
