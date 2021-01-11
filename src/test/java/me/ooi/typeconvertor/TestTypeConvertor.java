package me.ooi.typeconvertor;

import static me.ooi.typeconvertor.TestUtils.dayEq;
import static me.ooi.typeconvertor.TestUtils.orclTime2SqlTime;
import static me.ooi.typeconvertor.TestUtils.timeEq;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Test;

import me.ooi.typeconvertor.util.ClassUtils;

/**
 * @author jun.zhao
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TestTypeConvertor {
	
	//数字类型
	private static final Class[] NUMBER_CLASSES = new Class[] {
			Short.class, Byte.class, Integer.class, Long.class, Float.class, Double.class, 
			BigDecimal.class, BigInteger.class};
	
	//小数类型
	private static final Class[] DECIMAL_CLASSES = new Class[] {Float.class, Double.class,BigDecimal.class};
	
	//约等于
	public static boolean roughlyEq(Number left, Number right) {
		return BigDecimal.valueOf(left.doubleValue()).setScale(4, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(right.doubleValue()).setScale(4, RoundingMode.HALF_UP)) == 0;
	}
	
	//等于
	public static boolean eq(Number left, Number right) {
		return BigDecimal.valueOf(left.doubleValue()).compareTo(BigDecimal.valueOf(right.doubleValue())) == 0;
	}
	
	public void assertNumberConvert(Object source, Class<? extends Number> targetClass) {
		Object val = TypeConvertUtils.convert(source, targetClass);
		assert val != null;
		assert val.getClass() == targetClass;
		if( source.getClass() == String.class ) { //validate string to number
			if( val.getClass() == Float.class ) { //float到double会不准，这里用约等于
				assert roughlyEq(new BigDecimal((String)source), (Number)val);
			}else {
				assert eq(new BigDecimal((String)source), (Number)val);
			}
		}else { //validate number to number
			assert Number.class.isAssignableFrom(source.getClass());
			if( source.getClass() == Float.class || val.getClass() == Float.class) { //float到double会不准，这里用约等于
				assert roughlyEq((Number)source, (Number)val);
			}else {
				assert eq((Number)source, (Number)val);
			}
		}
	}
	
	public void assertDateConvertor(Object source, Class targetClass) {
		Object val = TypeConvertUtils.convert(source, targetClass);
		assert val != null;
		assert targetClass.isAssignableFrom(val.getClass());
	}
	
	public void testNumber(Object source) {
		for (int i = 0; i < NUMBER_CLASSES.length; i++) {
			assertNumberConvert(source, NUMBER_CLASSES[i]);
		}
	}
	
	public void testDecimal(Object source) {
		for (int i = 0; i < DECIMAL_CLASSES.length; i++) {
			assertNumberConvert(source, DECIMAL_CLASSES[i]);
		}
	}
	
	@Test
	public void testNumber() {
		int source = 10;
		testNumber((short) source);
		testNumber((byte)source);
		testNumber((int)source);
		testNumber((long)source);
		testNumber((float)source);
		testNumber((double)source);
		testNumber(BigDecimal.valueOf(source));
		testNumber(BigInteger.valueOf(source));
	}
	
	//float到double会不准
	@Test
	public void testDecimal() {
		double source = .05D;
		testDecimal((float)source);
		testDecimal((double)source);
		testDecimal(BigDecimal.valueOf(source));
	}
	
	@Test
	public void testString2Number() {
		String s = "10";
		testNumber(s);
	}
	
	@Test
	public void testString2Number2() {
		String s = "0.05";
		testDecimal(s);
	}
	
	@Test
	public void testNumber2ItSelf() {
		float f = .05f;
		Object val = TypeConvertUtils.convert(f, Number.class);
		assert val != null;
		assert val.getClass() == Float.class;
		assert eq(f, (Number)val);
	}
	
	@Test
	public void testOutofRange() {
		int source = Integer.MAX_VALUE;
		Exception e = null;
		try {
			TypeConvertUtils.convert(source, Byte.class);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			e = ex;
		}
		assert e != null;
	}
	
	@Test
	public void testEmptyStringToNumber() {
		Object val = TypeConvertUtils.convert("", Byte.class);
		assert val == null;
	}
	
	@Test
	public void testTargetIsNull() {
		String source = "10";
		Object val = TypeConvertUtils.convert(source, null);
		assert val == null;
	}
	
	@Test
	public void testSourceIsNull() {
		Object val = TypeConvertUtils.convert(null, String.class);
		assert val == null;
		val = TypeConvertUtils.convert(null, Integer.class);
		assert val == null;
	}
	
	@Test
	public void testChild2Parent() {
		C c = new C();
		Object val = TypeConvertUtils.convert(c, P.class);
		assert val != null;
		assert val.getClass() == C.class;
	}
	
	@Test
	public void testRegistyConvertor() {
		Class<?> oracleTimestampClass = ClassUtils.forName("oracle.sql.TIMESTAMP");
		
		//from java.util.Date.class to others
		java.util.Date date = new java.util.Date();
		assertDateConvertor(date, java.sql.Date.class);
		assertDateConvertor(date, java.sql.Timestamp.class);
		if( oracleTimestampClass != null ) {
			assertDateConvertor(date, oracleTimestampClass);
		}
		
		//from java.sql.Date.class to others
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		assertDateConvertor(sqlDate, java.util.Date.class);
		assertDateConvertor(sqlDate, java.sql.Timestamp.class);
		if( oracleTimestampClass != null ) {
			assertDateConvertor(sqlDate, oracleTimestampClass);
		}
		
		//from java.sql.Timestamp.class to others
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(date.getTime());
		assertDateConvertor(sqlTimestamp, java.util.Date.class);
		assertDateConvertor(sqlTimestamp, java.sql.Date.class);
		if( oracleTimestampClass != null ) {
			assertDateConvertor(sqlTimestamp, oracleTimestampClass);
		}
		
		//from oracle.sql.TIMESTAMP.class to others
		if( oracleTimestampClass != null ) {
			Object oracleTimestamp = TypeConvertUtils.convert(date, oracleTimestampClass);
			assert oracleTimestamp != null;
			assert oracleTimestamp.getClass() == oracleTimestampClass;
			
			assertDateConvertor(oracleTimestamp, java.util.Date.class);
			assertDateConvertor(oracleTimestamp, java.sql.Date.class);
			assertDateConvertor(oracleTimestamp, java.sql.Timestamp.class);
		}
	}
	
	@Test
	public void testRegistyConvertor2() {
		Class<?> oracleTimestampClass = ClassUtils.forName("oracle.sql.TIMESTAMP");
		
		//from java.util.Date.class to others
		java.util.Date date = new java.util.Date();
		Class targetClass = java.sql.Date.class;
		Object val = TypeConvertUtils.convert(date, targetClass);
		assert val != null;
		assert targetClass.isAssignableFrom(val.getClass());
		timeEq(date, (java.util.Date)val);
		
		targetClass = java.sql.Timestamp.class;
		val = TypeConvertUtils.convert(date, java.sql.Timestamp.class);
		assert val != null;
		assert targetClass.isAssignableFrom(val.getClass());
		timeEq(date, (java.util.Date)val);
		
		if( oracleTimestampClass != null ) {
			targetClass = oracleTimestampClass;
			val = TypeConvertUtils.convert(date, targetClass);
			assert val != null;
			assert targetClass.isAssignableFrom(val.getClass());
			timeEq(date, orclTime2SqlTime(val));
		}
		
		//from java.sql.Date.class to others
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		targetClass = java.sql.Date.class;
		val = TypeConvertUtils.convert(sqlDate, targetClass);
		assert val != null;
		assert targetClass.isAssignableFrom(val.getClass());
		dayEq(sqlDate, (java.util.Date)val);
		
		targetClass = java.sql.Timestamp.class;
		val = TypeConvertUtils.convert(sqlDate, targetClass);
		assert val != null;
		assert targetClass.isAssignableFrom(val.getClass());
		dayEq(sqlDate, (java.util.Date)val);
		
		if( oracleTimestampClass != null ) {
			targetClass = oracleTimestampClass;
			val = TypeConvertUtils.convert(sqlDate, targetClass);
			assert val != null;
			assert targetClass.isAssignableFrom(val.getClass());
			dayEq(sqlDate, orclTime2SqlTime(val));
		}
		
		//from java.sql.Timestamp.class to others
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(date.getTime());
		targetClass = java.util.Date.class;
		val = TypeConvertUtils.convert(sqlDate, targetClass);
		assert val != null;
		assert targetClass.isAssignableFrom(val.getClass());
		timeEq(sqlTimestamp, (java.util.Date)val);
		
		targetClass = java.sql.Date.class;
		val = TypeConvertUtils.convert(sqlDate, targetClass);
		assert val != null;
		assert targetClass.isAssignableFrom(val.getClass());
		timeEq(sqlTimestamp, (java.util.Date)val);
		
		if( oracleTimestampClass != null ) {
			targetClass = oracleTimestampClass;
			val = TypeConvertUtils.convert(sqlDate, targetClass);
			assert val != null;
			assert targetClass.isAssignableFrom(val.getClass());
			timeEq(sqlTimestamp, orclTime2SqlTime(val));
		}
		
		//from oracle.sql.TIMESTAMP.class to others
		if( oracleTimestampClass != null ) {
			Object oracleTimestamp = TypeConvertUtils.convert(date, oracleTimestampClass);
			assert oracleTimestamp != null;
			assert oracleTimestamp.getClass() == oracleTimestampClass;
			
			targetClass = java.util.Date.class;
			val = TypeConvertUtils.convert(sqlDate, targetClass);
			assert val != null;
			assert targetClass.isAssignableFrom(val.getClass());
			timeEq(orclTime2SqlTime(oracleTimestamp), (java.util.Date)val);
			
			targetClass = java.sql.Date.class;
			val = TypeConvertUtils.convert(sqlDate, targetClass);
			assert val != null;
			assert targetClass.isAssignableFrom(val.getClass());
			dayEq(orclTime2SqlTime(oracleTimestamp), (java.util.Date)val);
			
			targetClass = java.sql.Timestamp.class;
			val = TypeConvertUtils.convert(sqlDate, targetClass);
			assert val != null;
			assert targetClass.isAssignableFrom(val.getClass());
			dayEq(orclTime2SqlTime(oracleTimestamp), (java.util.Date)val);
		}
	}
	
	public static class P{
	}
	public static class C extends P{
	}
	
}
