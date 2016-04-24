package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface Execute<Statement extends PreparedStatement>
        extends ParameterProvider<Execute<Statement>, Statement> {

    boolean execute();

}
