package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface UpdateParameterProvider<Statement extends PreparedStatement>
        extends ParameterProvider<UpdateParameterProvider<Statement>, Statement> {

    int count();

    long largeCount();

}
