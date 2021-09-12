package com.zeroone.ast;

import org.hibernate.Filter;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.loader.hql.QueryLoader;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TenantQueryLoader extends QueryLoader {
    /**
     * Creates a new Loader implementation.
     *
     * @param queryTranslator The query translator that is the delegator.
     * @param factory         The factory from which this loader is being created.
     * @param selectClause    The AST representing the select clause for loading.
     */
    public TenantQueryLoader(QueryTranslatorImpl queryTranslator, SessionFactoryImplementor factory, SelectClause selectClause) {
        super(queryTranslator, factory, selectClause);
    }


    @Override
    protected int bindParameterValues(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException {

        Filter filter = session.getLoadQueryInfluencers().enableFilter(TenancyConstants.TENANT_FILTER);
        filter.setParameter(TenancyConstants.MERCHANT_ID,"100");
        return super.bindParameterValues(statement, queryParameters, startIndex, session);
    }
}
