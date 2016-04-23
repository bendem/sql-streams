package be.bendem.sqlstreams;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public interface ParameterProvider<Provider extends ParameterProvider<Provider, Statement>, Statement extends PreparedStatement>
        extends StatementHolder<Statement> {

    Provider prepare(SqlConsumer<Statement> preparator);

    Provider setMagic(int index, Object x);

    Provider setArray(int index, Array x);
    Provider setAsciiStream(int index, InputStream x);
    Provider setAsciiStream(int index, InputStream x, int length);
    Provider setAsciiStream(int index, InputStream x, long length);
    Provider setBigDecimal(int index, BigDecimal x);
    Provider setBinaryStream(int index, InputStream x);
    Provider setBinaryStream(int index, InputStream x, int length);
    Provider setBinaryStream(int index, InputStream x, long length);
    Provider setBlob(int index, Blob x);
    Provider setBlob(int index, InputStream inputStream);
    Provider setBlob(int index, InputStream inputStream, long length);
    Provider setBoolean(int index, boolean x);
    Provider setByte(int index, byte x);
    Provider setBytes(int index, byte x[]);
    Provider setCharacterStream(int index, Reader reader);
    Provider setCharacterStream(int index, Reader reader, int length);
    Provider setCharacterStream(int index, Reader reader, long length);
    Provider setClob(int index, Clob x);
    Provider setClob(int index, Reader reader);
    Provider setClob(int index, Reader reader, long length);
    Provider setDate(int index, Date x);
    Provider setDate(int index, Date x, Calendar cal);
    Provider setDouble(int index, double x);
    Provider setFloat(int index, float x);
    Provider setInt(int index, int x);
    Provider setLong(int index, long x);
    Provider setNCharacterStream(int index, Reader value);
    Provider setNCharacterStream(int index, Reader value, long length);
    Provider setNClob(int index, NClob value);
    Provider setNClob(int index, Reader reader);
    Provider setNClob(int index, Reader reader, long length);
    Provider setNString(int index, String value);
    Provider setNull(int index, int sqlType);
    Provider setNull(int index, int sqlType, String typeName);
    Provider setObject(int index, Object x);
    Provider setObject(int index, Object x, int targetSqlType);
    Provider setObject(int index, Object x, int targetSqlType, int scaleOrLength);
    Provider setObject(int index, Object x, SQLType targetSqlType);
    Provider setObject(int index, Object x, SQLType targetSqlType, int scaleOrLength);
    Provider setRef(int index, Ref x);
    Provider setRowId(int index, RowId x);
    Provider setShort(int index, short x);
    Provider setSQLXML(int index, SQLXML xmlObject);
    Provider setString(int index, String x);
    Provider setTime(int index, java.sql.Time x);
    Provider setTime(int index, java.sql.Time x, Calendar cal);
    Provider setTimestamp(int index, java.sql.Timestamp x);
    Provider setTimestamp(int index, java.sql.Timestamp x, Calendar cal);
    Provider setURL(int index, URL x);

}
