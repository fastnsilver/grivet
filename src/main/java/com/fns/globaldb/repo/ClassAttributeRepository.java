package com.fns.globaldb.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fns.globaldb.model.ClassAttribute;

@Repository
public interface ClassAttributeRepository extends PagingAndSortingRepository<ClassAttribute, Integer> {

    public List<ClassAttribute> findByCid(@Param("cid") Integer cid);
    
    public ClassAttribute findByCidAndAid(@Param("cid") Integer cid, @Param("aid") Integer aid);
}
