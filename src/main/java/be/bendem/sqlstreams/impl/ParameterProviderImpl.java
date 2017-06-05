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

    ParameterProviderImpl(Statement statement) {
        this.statement = statement;
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
        Wrap.execute(() -> SqlBindings.map(statement, params, 0));
        return (Provider) this;
    }

    @Override
    public Provider set(int index, Object x) {
        Wrap.execute(() -> SqlBindings.map(statement, index, Objects.requireNonNull(x)));
        return (Provider) this;
    }

    @Override
    public Provider setArray(int index, Array x) {
        Wrap.execute(() -> statement.setArray(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setAsciiStream(int index, InputStream x) {
        Wrap.execute(() -> statement.setAsciiStream(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setAsciiStream(int index, InputStream x, int length) {
        Wrap.execute(() -> statement.setAsciiStream(index, x, length));
        return (Provider) this;
    }

    @Override
    public Provider setAsciiStream(int index, InputStream x, long length) {
        Wrap.execute(() -> statement.setAsciiStream(index, x, length));
        return (Provider) this;
    }

    @Override
    public Provider setBigDecimal(int index, BigDecimal x) {
        Wrap.execute(() -> statement.setBigDecimal(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setBinaryStream(int index, InputStream x) {
        Wrap.execute(() -> statement.setBinaryStream(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setBinaryStream(int index, InputStream x, int length) {
        Wrap.execute(() -> statement.setBinaryStream(index, x, length));
        return (Provider) this;
    }

    @Override
    public Provider setBinaryStream(int index, InputStream x, long length) {
        Wrap.execute(() -> statement.setBinaryStream(index, x, length));
        return (Provider) this;
    }

    @Override
    public Provider setBlob(int index, Blob x) {
        Wrap.execute(() -> statement.setBlob(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setBlob(int index, InputStream inputStream) {
        Wrap.execute(() -> statement.setBlob(index, inputStream));
        return (Provider) this;
    }

    @Override
    public Provider setBlob(int index, InputStream inputStream, long length) {
        Wrap.execute(() -> statement.setBlob(index, inputStream, length));
        return (Provider) this;
    }

    @Override
    public Provider setBoolean(int index, boolean x) {
        Wrap.execute(() -> statement.setBoolean(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setByte(int index, byte x) {
        Wrap.execute(() -> statement.setByte(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setBytes(int index, byte[] x) {
        Wrap.execute(() -> statement.setBytes(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setCharacterStream(int index, Reader reader) {
        Wrap.execute(() -> statement.setCharacterStream(index, reader));
        return (Provider) this;
    }

    @Override
    public Provider setCharacterStream(int index, Reader reader, int length) {
        Wrap.execute(() -> statement.setCharacterStream(index, reader, length));
        return (Provider) this;
    }

    @Override
    public Provider setCharacterStream(int index, Reader reader, long length) {
        Wrap.execute(() -> statement.setCharacterStream(index, reader, length));
        return (Provider) this;
    }

    @Override
    public Provider setClob(int index, Clob x) {
        Wrap.execute(() -> statement.setClob(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setClob(int index, Reader reader) {
        Wrap.execute(() -> statement.setClob(index, reader));
        return (Provider) this;
    }

    @Override
    public Provider setClob(int index, Reader reader, long length) {
        Wrap.execute(() -> statement.setClob(index, reader, length));
        return (Provider) this;
    }

    @Override
    public Provider setDate(int index, Date x) {
        Wrap.execute(() -> statement.setDate(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setDate(int index, Date x, Calendar cal) {
        Wrap.execute(() -> statement.setDate(index, x, cal));
        return (Provider) this;
    }

    @Override
    public Provider setDouble(int index, double x) {
        Wrap.execute(() -> statement.setDouble(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setFloat(int index, float x) {
        Wrap.execute(() -> statement.setFloat(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setInt(int index, int x) {
        Wrap.execute(() -> statement.setInt(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setLong(int index, long x) {
        Wrap.execute(() -> statement.setLong(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setNCharacterStream(int index, Reader value) {
        Wrap.execute(() -> statement.setNCharacterStream(index, value));
        return (Provider) this;
    }

    @Override
    public Provider setNCharacterStream(int index, Reader value, long length) {
        Wrap.execute(() -> statement.setNCharacterStream(index, value, length));
        return (Provider) this;
    }

    @Override
    public Provider setNClob(int index, NClob value) {
        Wrap.execute(() -> statement.setNClob(index, value));
        return (Provider) this;
    }

    @Override
    public Provider setNClob(int index, Reader reader) {
        Wrap.execute(() -> statement.setNClob(index, reader));
        return (Provider) this;
    }

    @Override
    public Provider setNClob(int index, Reader reader, long length) {
        Wrap.execute(() -> statement.setNClob(index, reader, length));
        return (Provider) this;
    }

    @Override
    public Provider setNString(int index, String value) {
        Wrap.execute(() -> statement.setNString(index, value));
        return (Provider) this;
    }

    @Override
    public Provider setNull(int index, int sqlType) {
        Wrap.execute(() -> statement.setNull(index, sqlType));
        return (Provider) this;
    }

    @Override
    public Provider setNull(int index, int sqlType, String typeName) {
        Wrap.execute(() -> statement.setNull(index, sqlType, typeName));
        return (Provider) this;
    }

    @Override
    public Provider setObject(int index, Object x) {
        Wrap.execute(() -> statement.setObject(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setObject(int index, Object x, int targetSqlType) {
        Wrap.execute(() -> statement.setObject(index, x, targetSqlType));
        return (Provider) this;
    }

    @Override
    public Provider setObject(int index, Object x, int targetSqlType, int scaleOrLength) {
        Wrap.execute(() -> statement.setObject(index, x, targetSqlType, scaleOrLength));
        return (Provider) this;
    }

    @Override
    public Provider setObject(int index, Object x, SQLType targetSqlType) {
        Wrap.execute(() -> statement.setObject(index, x, targetSqlType));
        return (Provider) this;
    }

    @Override
    public Provider setObject(int index, Object x, SQLType targetSqlType, int scaleOrLength) {
        Wrap.execute(() -> statement.setObject(index, x, targetSqlType, scaleOrLength));
        return (Provider) this;
    }

    @Override
    public Provider setRef(int index, Ref x) {
        Wrap.execute(() -> statement.setRef(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setRowId(int index, RowId x) {
        Wrap.execute(() -> statement.setRowId(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setShort(int index, short x) {
        Wrap.execute(() -> statement.setShort(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setSQLXML(int index, SQLXML xmlObject) {
        Wrap.execute(() -> statement.setSQLXML(index, xmlObject));
        return (Provider) this;
    }

    @Override
    public Provider setString(int index, String x) {
        Wrap.execute(() -> statement.setString(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setTime(int index, Time x) {
        Wrap.execute(() -> statement.setTime(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setTime(int index, Time x, Calendar cal) {
        Wrap.execute(() -> statement.setTime(index, x, cal));
        return (Provider) this;
    }

    @Override
    public Provider setTimestamp(int index, Timestamp x) {
        Wrap.execute(() -> statement.setTimestamp(index, x));
        return (Provider) this;
    }

    @Override
    public Provider setTimestamp(int index, Timestamp x, Calendar cal) {
        Wrap.execute(() -> statement.setTimestamp(index, x, cal));
        return (Provider) this;
    }

    @Override
    public Provider setURL(int index, URL x) {
        Wrap.execute(() -> statement.setURL(index, x));
        return (Provider) this;
    }

}
