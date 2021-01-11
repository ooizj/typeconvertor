package me.ooi.typeconvertor;

import java.lang.reflect.Constructor;

/**
 * convert <b>java.util.Date</b> to <b>oracle.sql.TIMESTAMP</b>
 * @author jun.zhao
 * @since 1.0
 */
public class UtilDate2OracleTimestampTypeConvertor implements ITypeConvertor<Object>{

	@Override
	public Object convert(Object source) {
		if( source == null ){
			return null;
		}
		
		if( !java.util.Date.class.isAssignableFrom(source.getClass()) ){
			throw new IllegalArgumentException("parameter must be java.util.Date or extend java.util.Date.");
		}
		
		Constructor<?> constructor;
		try {
			constructor = Class.forName("oracle.sql.TIMESTAMP").getDeclaredConstructor(java.sql.Timestamp.class);
			return constructor.newInstance(new java.sql.Timestamp(((java.util.Date)source).getTime()));
		} catch (Exception e) {
			throw new TypeConvertException(e);
		} 
	}

}
