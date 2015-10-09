package de.rwth.dbis.acis.activitytracker.service.dal;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;

public class DALFacadeImpl implements DALFacade {

    private final DSLContext dslContext;
    private final Connection connection;


    public DALFacadeImpl(Connection connection, SQLDialect dialect) {
        this.connection = connection;
        dslContext = DSL.using(connection, dialect);
    }

    public DSLContext getDslContext() {
        return dslContext;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
