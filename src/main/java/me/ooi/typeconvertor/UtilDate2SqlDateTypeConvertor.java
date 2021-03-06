package me.ooi.typeconvertor;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class UtilDate2SqlDateTypeConvertor implements ITypeConvertor<java.sql.Date>{

	@Override
	public java.sql.Date convert(Object source) {
		if( source == null ){
			return null;
		}
		
		if( !java.util.Date.class.isAssignableFrom(source.getClass()) ){
			throw new IllegalArgumentException("parameter must be java.util.Date or extend java.util.Date.");
		}
		
		return new java.sql.Date(((java.util.Date)source).getTime());
	}

	
	

}
