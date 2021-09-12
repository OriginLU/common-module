package com.zeroone.repository;

import com.zeroone.entity.Xa01;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface Xa01Repository extends JpaRepository<Xa01,Long>, JpaSpecificationExecutor<Xa01> {
}
