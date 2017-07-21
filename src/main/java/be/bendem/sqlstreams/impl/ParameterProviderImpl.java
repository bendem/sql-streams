package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.ParameterProvider;
import be.bendem.sqlstreams.util.SqlConsumer;
import be.bendem.sqlstreams.util.Wrap;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Objects;

@SuppressWarnings("unchecked")
class ParameterProviderImpl<Provider extends ParameterProvider<Provider, Statement>, Statement extends PreparedStatement>
        implements ParameterProvider<Provider, Statement> {

    protected final Statement statement;
    private final SqlBindings bindings;

    ParameterProviderImpl(Statement statement, SqlBindings bindings) {
        this.statement = statement;
        this.bindings = bindings;
    }

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    public Provider prepare(SqlConsumer<Statement> preparator) {
        Wrap.execute(() -> preparator.accept(statement));
        return (Provider) this;
    }

    @Override
    public Provider with(Object... params) {
        return prepare(statement -> bindings.bind(statement, params, 0));
    }

    @Override
    public Provider set(int index, Object x) {
        return prepare(statement -> bindings.bind(statement, index, Objects.requireNonNull(x)));
    }

    @Override
    public Provider setArray(int index, Array x) {
        return prepare(statement -> statement.setArray(index, x));
    }

    @Override
    public Provider setAsciiStream(int index, InputStream x) {
        return prepare(statement -> statement.setAsciiStream(index, x));
    }

    @Override
    public Provider setAsciiStream(int index, InputStream x, int length) {
        return prepare(statement -> statement.setAsciiStream(index, x, length));
    }

    @Override
    public Provider setAsciiStream(int index, InputStream x, long length) {
        return prepare(statement -> statement.setAsciiStream(index, x, length));
    }

    @Override
    public Provider setBigDecimal(int index, BigDecimal x) {
        return prepare(statement -> statement.setBigDecimal(index, x));
    }

    @Override
    public Provider setBinaryStream(int index, InputStream x) {
        return prepare(statement -> statement.setBinaryStream(index, x));
    }

    @Override
    public Provider setBinaryStream(int index, InputStream x, int length) {
        return prepare(statement -> statement.setBinaryStream(index, x, length));
    }

    @Override
    public Provider setBinaryStream(int index, InputStream x, long length) {
        return prepare(statement -> statement.setBinaryStream(index, x, length));
    }

    @Override
    public Provider setBlob(int index, Blob x) {
        return prepare(statement -> statement.setBlob(index, x));
    }

    @Override
    public Provider setBlob(int index, InputStream inputStream) {
        return prepare(statement -> statement.setBlob(index, inputStream));
    }

    @Override
    public Provider setBlob(int index, InputStream inputStream, long length) {
        return prepare(statement -> statement.setBlob(index, inputStream, length));
    }

    @Override
    public Provider setBoolean(int index, boolean x) {
        return prepare(statement -> statement.setBoolean(index, x));
    }

    @Override
    public Provider setByte(int index, byte x) {
        return prepare(statement -> statement.setByte(index, x));
    }

    @Override
    public Provider setBytes(int index, byte[] x) {
        return prepare(statement -> statement.setBytes(index, x));
    }

    @Override
    public Provider setCharacterStream(int index, Reader reader) {
        return prepare(statement -> statement.setCharacterStream(index, reader));
    }

    @Override
    public Provider setCharacterStream(int index, Reader reader, int length) {
        return prepare(statement -> statement.setCharacterStream(index, reader, length));
    }

    @Override
    public Provider setCharacterStream(int index, Reader reader, long length) {
        return prepare(statement -> statement.setCharacterStream(index, reader, length));
    }

    @Override
    public Provider setClob(int index, Clob x) {
        return prepare(statement -> statement.setClob(index, x));
    }

    @Override
    public Provider setClob(int index, Reader reader) {
        return prepare(statement -> statement.setClob(index, reader));
    }

    @Override
    public Provider setClob(int index, Reader reader, long length) {
        return prepare(statement -> statement.setClob(index, reader, length));
    }

    @Override
    public Provider setDate(int index, Date x) {
        return prepare(statement -> statement.setDate(index, x));
    }

    @Override
    public Provider setDate(int index, Date x, Calendar cal) {
        return prepare(statement -> statement.setDate(index, x, cal));
    }

    @Override
    public Provider setDouble(int index, double x) {
        return prepare(statement -> statement.setDouble(index, x));
    }

    @Override
    public Provider setFloat(int index, float x) {
        return prepare(statement -> statement.setFloat(index, x));
    }

    @Override
    public Provider setInt(int index, int x) {
        return prepare(statement -> statement.setInt(index, x));
    }

    @Override
    public Provider setLong(int index, long x) {
        return prepare(statement -> statement.setLong(index, x));
    }

    @Override
    public Provider setNCharacterStream(int index, Reader value) {
        return prepare(statement -> statement.setNCharacterStream(index, value));
    }

    @Override
    public Provider setNCharacterStream(int index, Reader value, long length) {
        return prepare(statement -> statement.setNCharacterStream(index, value, length));
    }

    @Override
    public Provider setNClob(int index, NClob value) {
        return prepare(statement -> statement.setNClob(index, value));
    }

    @Override
    public Provider setNClob(int index, Reader reader) {
        return prepare(statement -> statement.setNClob(index, reader));
    }

    @Override
    public Provider setNClob(int index, Reader reader, long length) {
        return prepare(statement -> statement.setNClob(index, reader, length));
    }

    @Override
    public Provider setNString(int index, String value) {
        return prepare(statement -> statement.setNString(index, value));
    }

    @Override
    public Provider setNull(int index, int sqlType) {
        return prepare(statement -> statement.setNull(index, sqlType));
    }

    @Override
    public Provider setNull(int index, int sqlType, String typeName) {
        return prepare(statement -> statement.setNull(index, sqlType, typeName));
    }

    @Override
    public Provider setObject(int index, Object x) {
        return prepare(statement -> statement.setObject(index, x));
    }

    @Override
    public Provider setObject(int index, Object x, int targetSqlType) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType));
    }

    @Override
    public Provider setObject(int index, Object x, int targetSqlType, int scaleOrLength) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType, scaleOrLength));
    }

    @Override
    public Provider setObject(int index, Object x, SQLType targetSqlType) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType));
    }

    @Override
    public Provider setObject(int index, Object x, SQLType targetSqlType, int scaleOrLength) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType, scaleOrLength));
    }

    @Override
    public Provider setRef(int index, Ref x) {
        return prepare(statement -> statement.setRef(index, x));
    }

    @Override
    public Provider setRowId(int index, RowId x) {
        return prepare(statement -> statement.setRowId(index, x));
    }

    @Override
    public Provider setShort(int index, short x) {
        return prepare(statement -> statement.setShort(index, x));
    }

    @Override
    public Provider setSQLXML(int index, SQLXML xmlObject) {
        return prepare(statement -> statement.setSQLXML(index, xmlObject));
    }

    @Override
    public Provider setString(int index, String x) {
        return prepare(statement -> statement.setString(index, x));
    }

    @Override
    public Provider setTime(int index, Time x) {
        return prepare(statement -> statement.setTime(index, x));
    }

    @Override
    public Provider setTime(int index, Time x, Calendar cal) {
        return prepare(statement -> statement.setTime(index, x, cal));
    }

    @Override
    public Provider setTimestamp(int index, Timestamp x) {
        return prepare(statement -> statement.setTimestamp(index, x));
    }

    @Override
    public Provider setTimestamp(int index, Timestamp x, Calendar cal) {
        return prepare(statement -> statement.setTimestamp(index, x, cal));
    }

    @Override
    public Provider setURL(int index, URL x) {
        return prepare(statement -> statement.setURL(index, x));
    }
}
