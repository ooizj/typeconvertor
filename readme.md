a type convertor.

## usage
```java
Object val = TypeConvertUtils.convert(source, targetClass);
System.out.println(String.format("convert: %s -> %s", source.getClass(), targetClass));
System.out.println(String.format("result: %s:%s", val.getClass(), val));
if( targetClass != val.getClass() ) {
	System.err.println(String.format("error: target class(%s), actual class(%s) ", targetClass, val.getClass()));
}
System.out.println();
```

### 数字之间的转换
support list:

```java
System.out.println(TypeConvertUtils.convert(10, Double.class)); // Double:10
System.out.println(TypeConvertUtils.convert(10, double.class)); // double:10
System.out.println(TypeConvertUtils.convert(10, double.class)); // double:10
```

