package me.ooi.typeconvertor;

import java.sql.Timestamp;

/**
 * convert <b>oracle.sql.TIMESTAMP</b> to <b>java.sql.Timestamp</b>
 * @author jun.zhao
 * @since 1.0
 */
public class OracleTimestamp2SqlTimestampTypeConvertor implements ITypeConvertor<Object>{

	@Override
	public Object convert(Object source) {
		if( source == null ){
			return null;
		}
		
		Class<?> oracleTimestampClass = null;
		try {
			oracleTimestampClass = Class.forName("oracle.sql.TIMESTAMP");
		} catch (ClassNotFoundException e) {
			throw new TypeConvertException(e);
		}
		
		if( !oracleTimestampClass.isAssignableFrom(source.getClass()) ){
			throw new IllegalArgumentException("parameter must be oracle.sql.TIMESTAMP or extend oracle.sql.TIMESTAMP.");
		}
		
		try {
			return (Timestamp)oracleTimestampClass.getMethod("timestampValue").invoke(source);
		} catch (Exception e) {
			throw new TypeConvertException(e);
		}
	}

}
