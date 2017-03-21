package com.fns.grivet.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fns.grivet.model.ClassAttribute;

@Repository
public interface ClassAttributeRepository extends PagingAndSortingRepository<ClassAttribute, Integer> {

    public List<ClassAttribute> findByCid(@Param("cid") Integer cid);
    
    public ClassAttribute findByCidAndAid(@Param("cid") Integer cid, @Param("aid") Integer aid);
}
