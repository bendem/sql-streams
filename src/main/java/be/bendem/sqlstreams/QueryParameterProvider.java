package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface QueryParameterProvider<Statement extends PreparedStatement>
        extends ParameterProvider<QueryParameterProvider<Statement>, Statement>, SqlResult {

}
