package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.SqlConsumer;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * Represents an object holding a {@link PreparedStatement} and providing methods
 * to set it up for a sql query.
 *
 * @param <Provider> the type of the parent to return from each method of this class
 * @param <Statement> the type of the statement
 */
public interface ParameterProvider<Provider extends ParameterProvider<Provider, Statement>, Statement extends PreparedStatement>
        extends StatementHolder<Statement> {

    /**
     * Prepares an instance of {@link PreparedStatement} to be executed.
     *
     * @param preparator an operation to execute on the statement
     * @return {@code this} for chaining
     */
    Provider prepare(SqlConsumer<Statement> preparator);

    /**
     * Sets multiple parameters using magic bindings.
     *
     * @param params parameters to set
     * @return {@code this} for chaining
     * @see ParameterProvider#setMagic(int, Object)
     */
    Provider with(Object... params);

    /**
     * Sets the designated parameter based on the type of the parameter given.
     * <p>
     * Supported parameters are java primitives, {@link String}, {@link Date}, {@link
     * Time}, {@link Timestamp}, {@link java.time.LocalDate}, {@link java.time.LocalTime}
     * and {@link java.time.LocalDateTime}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @throws NullPointerException if {@code x} is {@code null}
     */
    Provider setMagic(int index, Object x);

    /**
     * Sets the designated parameter to the given {@link Array}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setArray(int, Array)
     */
    Provider setArray(int index, Array x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setAsciiStream(int, InputStream)
     */
    Provider setAsciiStream(int index, InputStream x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setAsciiStream(int, InputStream, int)
     */
    Provider setAsciiStream(int index, InputStream x, int length);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setAsciiStream(int, InputStream, long)
     */
    Provider setAsciiStream(int index, InputStream x, long length);

    /**
     * Sets the designated parameter to the given {@link BigDecimal}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBigDecimal(int, BigDecimal)
     */
    Provider setBigDecimal(int index, BigDecimal x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBinaryStream(int, InputStream)
     */
    Provider setBinaryStream(int index, InputStream x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setBinaryStream(int, InputStream, int)
     */
    Provider setBinaryStream(int index, InputStream x, int length);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setBinaryStream(int, InputStream, long)
     */
    Provider setBinaryStream(int index, InputStream x, long length);

    /**
     * Sets the designated parameter to the given {@link Blob}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBlob(int, Blob)
     */
    Provider setBlob(int index, Blob x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBlob(int, InputStream)
     */
    Provider setBlob(int index, InputStream x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setBlob(int, InputStream, long)
     */
    Provider setBlob(int index, InputStream x, long length);

    /**
     * Sets the designated parameter to the given {@code boolean}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBoolean(int, boolean)
     */
    Provider setBoolean(int index, boolean x);

    /**
     * Sets the designated parameter to the given {@code byte}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setByte(int, byte)
     */
    Provider setByte(int index, byte x);

    /**
     * Sets the designated parameter to the given {@code byte[]}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBytes(int, byte[])
     */
    Provider setBytes(int index, byte x[]);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setCharacterStream(int, Reader)
     */
    Provider setCharacterStream(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setCharacterStream(int, Reader, int)
     */
    Provider setCharacterStream(int index, Reader x, int length);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setCharacterStream(int, Reader, long)
     */
    Provider setCharacterStream(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link Clob}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setClob(int, Clob)
     */
    Provider setClob(int index, Clob x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setClob(int, Reader)
     */
    Provider setClob(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setClob(int, Reader, long)
     */
    Provider setClob(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link Date}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setDate(int, Date)
     */
    Provider setDate(int index, Date x);

    /**
     * Sets the designated parameter to the given {@link Date}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param cal the {@link Calendar} object the driver will use to construct the date
     * @return {@code this} for chaining
     * @see PreparedStatement#setDate(int, Date, Calendar)
     */
    Provider setDate(int index, Date x, Calendar cal);

    /**
     * Sets the designated parameter to the given {@code double}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setDouble(int, double)
     */
    Provider setDouble(int index, double x);

    /**
     * Sets the designated parameter to the given {@code float}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setFloat(int, float)
     */
    Provider setFloat(int index, float x);

    /**
     * Sets the designated parameter to the given {@code int}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setInt(int, int)
     */
    Provider setInt(int index, int x);

    /**
     * Sets the designated parameter to the given {@code long}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setLong(int, long)
     */
    Provider setLong(int index, long x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNCharacterStream(int, Reader)
     */
    Provider setNCharacterStream(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setNCharacterStream(int, Reader, long length)
     */
    Provider setNCharacterStream(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link NClob}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNClob(int, NClob)
     */
    Provider setNClob(int index, NClob x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNClob(int, Reader)
     */
    Provider setNClob(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setNClob(int, Reader, long)
     */
    Provider setNClob(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link String}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNString(int, String)
     */
    Provider setNString(int index, String x);

    /**
     * Sets the designated parameter to the given {@link int}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param sqlType the SQL type code defined in {@link Types}
     * @return {@code this} for chaining
     * @see PreparedStatement#setNull(int, int)
     */
    Provider setNull(int index, int sqlType);

    /**
     * Sets the designated parameter to the given {@link int}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param sqlType the SQL type code defined in {@link Types}
     * @param typeName the fully-qualified name of an SQL user-defined type;
     *     ignored if the parameter is not a user-defined type or REF
     * @return {@code this} for chaining
     * @see PreparedStatement#setNull(int, int, String)
     */
    Provider setNull(int index, int sqlType, String typeName);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object)
     */
    Provider setObject(int index, Object x);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, int)
     */
    Provider setObject(int index, Object x, int targetSqlType);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @param scaleOrLength for {@code java.sql.Types.DECIMAL}
     *     or {@code java.sql.Types.NUMERIC}, this is the number of digits
     *     after the decimal point. For Java Object types {@link InputStream}
     *     and {@link Reader}, this is the length of the data in the stream
     *     or reader. For all other types, this value will be ignored.
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, int, int)
     */
    Provider setObject(int index, Object x, int targetSqlType, int scaleOrLength);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, SQLType)
     */
    Provider setObject(int index, Object x, SQLType targetSqlType);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @param scaleOrLength for {@code java.sql.Types.DECIMAL}
     *     or {@code java.sql.Types.NUMERIC}, this is the number of digits
     *     after the decimal point. For Java Object types {@link InputStream}
     *     and {@link Reader}, this is the length of the data in the stream
     *     or reader. For all other types, this value will be ignored.
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, SQLType, int)
     */
    Provider setObject(int index, Object x, SQLType targetSqlType, int scaleOrLength);

    /**
     * Sets the designated parameter to the given {@link Ref}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setRef(int, Ref)
     */
    Provider setRef(int index, Ref x);

    /**
     * Sets the designated parameter to the given {@link RowId}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setRowId(int, RowId)
     */
    Provider setRowId(int index, RowId x);

    /**
     * Sets the designated parameter to the given {@code short}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setShort(int, short)
     */
    Provider setShort(int index, short x);

    /**
     * Sets the designated parameter to the given {@link SQLXML}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setSQLXML(int, SQLXML)
     */
    Provider setSQLXML(int index, SQLXML x);

    /**
     * Sets the designated parameter to the given {@link String}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setString(int, String)
     */
    Provider setString(int index, String x);

    /**
     * Sets the designated parameter to the given {@link Time}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setTime(int, Time)
     */
    Provider setTime(int index, Time x);

    /**
     * Sets the designated parameter to the given {@link Time}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param cal the {@link Calendar} object the driver will use to construct the date
     * @return {@code this} for chaining
     * @see PreparedStatement#setTime(int, Time, Calendar)
     */
    Provider setTime(int index, Time x, Calendar cal);

    /**
     * Sets the designated parameter to the given {@link Timestamp}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setTimestamp(int, Timestamp)
     */
    Provider setTimestamp(int index, Timestamp x);

    /**
     * Sets the designated parameter to the given {@link Timestamp}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param cal the {@link Calendar} object the driver will use to construct the date
     * @return {@code this} for chaining
     * @see PreparedStatement#setTimestamp(int, Timestamp, Calendar)
     */
    Provider setTimestamp(int index, Timestamp x, Calendar cal);

    /**
     * Sets the designated parameter to the given {@link URL}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setURL(int, URL)
     */
    Provider setURL(int index, URL x);

}
