package com.zeroone.tenancy.hibernate.repository;

import com.zeroone.tenancy.hibernate.entity.TenantDataSourceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author zero-one.lu
 * @since 2020-04-03
 */
public interface TenantDataSourceInfoRepository extends JpaRepository<TenantDataSourceInfo,Integer> {

    Optional<List<TenantDataSourceInfo>> findByServerNameAndState(String serverName, Integer state);
}
