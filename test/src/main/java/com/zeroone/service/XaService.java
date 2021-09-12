package com.zeroone.service;

import com.zeroone.entity.Xa01;
import com.zeroone.repository.Xa01Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class XaService {


    @Autowired
    private Xa01Repository xa01Repository;


    @Autowired
    private EntityManagerFactory  entityManagerFactory;


    public List<Xa01> findAll(){

        return xa01Repository.findAll();
    }

    public Page<Xa01> findSpecification() {

        Specification<Xa01> specification =
                (root,query,cb) ->{

                    List<Predicate> predicates = new ArrayList<>();

                    predicates.add(cb.equal(root.get("name"),"test"));

                    return query.where(predicates.toArray(new Predicate[0])).getRestriction();
                };

        return xa01Repository.findAll(specification, PageRequest.of(0,10));
    }
}
