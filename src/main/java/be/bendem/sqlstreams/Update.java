package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface Update<Statement extends PreparedStatement>
        extends ParameterProvider<Update<Statement>, Statement> {

    int count();

    long largeCount();

}
