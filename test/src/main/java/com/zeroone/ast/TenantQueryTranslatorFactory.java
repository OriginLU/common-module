package com.zeroone.ast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.FilterImpl;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @see ASTQueryTranslatorFactory {@link ASTQueryTranslatorFactory}
 */
public class TenantQueryTranslatorFactory implements QueryTranslatorFactory {

    /**
     * Singleton access
     */
    public static final TenantQueryTranslatorFactory INSTANCE = new TenantQueryTranslatorFactory();


    private final Logger log = LoggerFactory.getLogger(getClass());


    private final ObjectMapper objectMapper = new ObjectMapper();


    private final Filter filter;

    public TenantQueryTranslatorFactory() {
        this.filter = getFilter();
    }

    private Filter getFilter() {

        Map<String, Type> parameterMap = new HashMap<>();
        parameterMap.put(TenancyConstants.MERCHANT_ID, new StringType());
        FilterDefinition filterDefinition = new FilterDefinition(TenancyConstants.TENANT_FILTER, TenancyConstants.CONDITION, parameterMap);
        return  new FilterImpl(filterDefinition);
    }

    @Override
    public QueryTranslator createQueryTranslator(
            String queryIdentifier,
            String queryString,
            Map filters,
            SessionFactoryImplementor factory,
            EntityGraphQueryHint entityGraphQueryHint) {
        log.info("enable filter：{}", getString(filters));

        return new TenantQueryTranslator(queryIdentifier, queryString,enableFilter(filters) , factory, entityGraphQueryHint);
    }

    @Override
    public FilterTranslator createFilterTranslator(
            String queryIdentifier,
            String queryString,
            Map filters,
            SessionFactoryImplementor factory) {
        log.info("enable filter：{}", getString(filters));

        return new TenantQueryTranslator(queryIdentifier, queryString, enableFilter(filters) , factory);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<String,Filter> enableFilter(Map filters){

        Map<String,Filter> enableFilters;
        if (!CollectionUtils.isEmpty(filters)){
            enableFilters = new HashMap<>(filters);
        }else {
            enableFilters = new HashMap<>();
        }

        enableFilters.put(TenancyConstants.TENANT_FILTER,filter);
        return enableFilters;
    }



    private String getString(Map filters) {
        try {
            return objectMapper.writeValueAsString(filters);
        } catch (JsonProcessingException e) {
            //ignore
            return "";
        }
    }


}
