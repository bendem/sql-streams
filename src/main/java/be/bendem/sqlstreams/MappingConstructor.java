package be.bendem.sqlstreams;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the constructor of an object as the one to use when no column
 * is mentioned. This constructor should only contains types supported
 * by {@link ParameterProvider#setMagic(int, Object)}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface MappingConstructor {
}
