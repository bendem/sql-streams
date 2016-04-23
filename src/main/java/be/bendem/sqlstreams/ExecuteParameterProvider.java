package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface ExecuteParameterProvider<Statement extends PreparedStatement>
        extends ParameterProvider<ExecuteParameterProvider<Statement>, Statement> {

    boolean execute();

}
