package me.ooi.typeconvertor;

import java.util.HashMap;
import java.util.Map;

import me.ooi.typeconvertor.util.ClassUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TypeConvertorRegistry {
	
	public static final TypeConvertorRegistry INSTANCE = new TypeConvertorRegistry();
	
	private Map<Class<?>, Map<Class<?>, ITypeConvertor<?>>> typeConvertorMap = new HashMap<Class<?>, Map<Class<?>, ITypeConvertor<?>>>();
	
	private Map<Class<?>, ITypeConvertor<?>> toTypeConvertorMap = new HashMap<Class<?>, ITypeConvertor<?>>();
	
	private TypeConvertorRegistry(){
		init();
	}
	
	private void init(){
		Class<?> oracleTimestampClass = ClassUtils.forName("oracle.sql.TIMESTAMP");
		
		//from java.util.Date.class to others
		Map<Class<?>, ITypeConvertor<?>> fromUtilDateTypeConvertMap = new HashMap<Class<?>, ITypeConvertor<?>>();
		fromUtilDateTypeConvertMap.put(java.sql.Date.class, new UtilDate2SqlDateTypeConvertor());
		fromUtilDateTypeConvertMap.put(java.sql.Timestamp.class, new UtilDate2SqlTimestampTypeConvertor());
		if( oracleTimestampClass != null ) {
			fromUtilDateTypeConvertMap.put(oracleTimestampClass, new UtilDate2OracleTimestampTypeConvertor());
		}
		typeConvertorMap.put(java.util.Date.class, fromUtilDateTypeConvertMap);
		
		//from java.sql.Date.class to others
		Map<Class<?>, ITypeConvertor<?>> fromSqlDateTypeConvertMap = new HashMap<Class<?>, ITypeConvertor<?>>();
		fromSqlDateTypeConvertMap.put(java.sql.Timestamp.class, new UtilDate2SqlTimestampTypeConvertor());
		if( oracleTimestampClass != null ) {
			fromSqlDateTypeConvertMap.put(oracleTimestampClass, new UtilDate2OracleTimestampTypeConvertor());
		}
		typeConvertorMap.put(java.sql.Date.class, fromSqlDateTypeConvertMap);
		
		//from java.sql.Timestamp.class to others
		Map<Class<?>, ITypeConvertor<?>> fromSqlTimestampTypeConvertMap = new HashMap<Class<?>, ITypeConvertor<?>>();
		fromSqlTimestampTypeConvertMap.put(java.sql.Date.class, new UtilDate2SqlDateTypeConvertor());
		if( oracleTimestampClass != null ) {
			fromSqlTimestampTypeConvertMap.put(oracleTimestampClass, new UtilDate2OracleTimestampTypeConvertor());
		}
		typeConvertorMap.put(java.sql.Timestamp.class, fromSqlTimestampTypeConvertMap);
		
		//from oracle.sql.TIMESTAMP.class to others
		if( oracleTimestampClass != null ) {
			Map<Class<?>, ITypeConvertor<?>> fromOracleTimestampTypeConvertMap = new HashMap<Class<?>, ITypeConvertor<?>>();
			fromOracleTimestampTypeConvertMap.put(java.util.Date.class, new OracleTimestamp2SqlTimestampTypeConvertor());
			fromOracleTimestampTypeConvertMap.put(java.sql.Timestamp.class, new OracleTimestamp2SqlTimestampTypeConvertor());
			fromOracleTimestampTypeConvertMap.put(java.sql.Date.class, new OracleTimestamp2SqlDateTypeConvertor());
			typeConvertorMap.put(oracleTimestampClass, fromOracleTimestampTypeConvertMap);
		}
		
	}
	
	public <TT> ITypeConvertor<TT> getTypeConvertor(Class<?> sourceType, Class<TT> targetType){
		ITypeConvertor<TT> typeConvertor = doGetTypeConvertor(sourceType, targetType);
		if( typeConvertor != null ){
			return typeConvertor;
		}
		
		return getTypeConvertor(targetType);
	}
	
	@SuppressWarnings("unchecked")
	private <TT> ITypeConvertor<TT> doGetTypeConvertor(Class<?> sourceType, Class<TT> targetType){
		Map<Class<?>, ITypeConvertor<?>> typeConvertMap = typeConvertorMap.get(sourceType);
		if( typeConvertMap == null ){
			return null;
		}
		
		return (ITypeConvertor<TT>) typeConvertMap.get(targetType);
	}
	
	@SuppressWarnings("unchecked")
	private <TT> ITypeConvertor<TT> getTypeConvertor(Class<TT> targetType){
		return (ITypeConvertor<TT>) toTypeConvertorMap.get(targetType);
	}
	
}
