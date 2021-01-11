package me.ooi.typeconvertor;

/**
 * convert <b>oracle.sql.TIMESTAMP</b> to <b>java.sql.Timestamp</b>
 * @author jun.zhao
 * @since 1.0
 */
public class OracleTimestamp2SqlDateTypeConvertor implements ITypeConvertor<Object>{

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
			return (java.sql.Date)oracleTimestampClass.getMethod("dateValue").invoke(source);
		} catch (Exception e) {
			throw new TypeConvertException(e);
		}
	}

}
