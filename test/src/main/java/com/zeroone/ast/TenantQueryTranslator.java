package com.zeroone.ast;

import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.loader.hql.QueryLoader;

import java.util.Map;

public class TenantQueryTranslator extends QueryTranslatorImpl {

    public TenantQueryTranslator(String queryIdentifier, String query, Map enabledFilters, SessionFactoryImplementor factory) {
        super(queryIdentifier, query, enabledFilters, factory);
    }

    public TenantQueryTranslator(String queryIdentifier, String query, Map enabledFilters, SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
        super(queryIdentifier, query, enabledFilters, factory, entityGraphQueryHint);
    }

    @Override
    protected QueryLoader createQueryLoader(HqlSqlWalker w, SessionFactoryImplementor factory) {
         return new TenantQueryLoader( this, factory, w.getSelectClause() );
    }
}
