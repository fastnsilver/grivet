package com.fns.grivet.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fns.grivet.model.ClassAttribute;

@RepositoryRestResource(collectionResourceRel = "classAttributes", path = "api/v1/classAttributes")
public interface ClassAttributeRepository extends PagingAndSortingRepository<ClassAttribute, Integer> {

    public List<ClassAttribute> findByCid(@Param("cid") Integer cid);
    
    public ClassAttribute findByCidAndAid(@Param("cid") Integer cid, @Param("aid") Integer aid);
}
