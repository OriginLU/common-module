package com.zeroone.tenancy.hibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zero-one.lu
 * @since 2020-04-03
 */
public interface TenantDataSourceInfoRepository extends JpaRepository<TenantDataSourceInfoRepository,Integer> {
}
