package me.ooi.typeconvertor;

import java.util.HashMap;
import java.util.Map;

import me.ooi.typeconvertor.util.NumberUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TypeConvertUtils {
	
	@SuppressWarnings("rawtypes")
	public static final Map<Class, Class> NUMBER_PRIMITIVE_TYPE_MAP = new HashMap<Class, Class>();
    static {
        NUMBER_PRIMITIVE_TYPE_MAP.put(byte.class, Byte.class);
        NUMBER_PRIMITIVE_TYPE_MAP.put(short.class, Short.class);
        NUMBER_PRIMITIVE_TYPE_MAP.put(int.class, Integer.class);
        NUMBER_PRIMITIVE_TYPE_MAP.put(long.class, Long.class);
        NUMBER_PRIMITIVE_TYPE_MAP.put(float.class, Float.class);
        NUMBER_PRIMITIVE_TYPE_MAP.put(double.class, Double.class);
    }
	
	/**
	 * 类型转换
	 * @param source
	 * @param targetType
	 * @return 如果source为NULL或targetType为NULL，或者不支持的转换，则返回NULL；<br>
	 * 如果没有定义source的类型到targetType的转换器，则source类型为targetType或targetType是source的父类直接返回该类型；<br>
	 * 如果是string到number，string為""，則返回null；<br>
	 * 如果targetType为double，source为float则结果会太一致；<br>
	 */
	public static Object convert(Object source, Class<?> targetType) {
		if( source == null || targetType == null ) {
			return null;
		}
		
		if( source.getClass() == targetType ) {
			return source;
		}
		
		if( NUMBER_PRIMITIVE_TYPE_MAP.containsKey(targetType) ) {
			targetType = NUMBER_PRIMITIVE_TYPE_MAP.get(targetType);
		}
		
		if( (source instanceof Number) && (Number.class.isAssignableFrom(targetType)) ) { //number to number
			return NumberUtils.convertNumberToTargetClass((Number)source, targetType);
		}else if( (source instanceof String) && (Number.class.isAssignableFrom(targetType)) ) { //string to number
			if( source.equals("") ) {
				return null;
			}
			return NumberUtils.parseNumber((String)source, targetType);
		}else if( targetType == String.class ) { //object to string
			return source.toString();
		}
		
		ITypeConvertor<?> typeConvertor = TypeConvertorRegistry.INSTANCE.getTypeConvertor(source.getClass(), targetType);
		if( typeConvertor != null ){
			return typeConvertor.convert(source);
		}
		
		if( targetType.isAssignableFrom(source.getClass()) ) {
			return source;
		}
		
		return null;
	}

}
