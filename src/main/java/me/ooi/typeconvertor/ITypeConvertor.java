package me.ooi.typeconvertor;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface ITypeConvertor<TT> {
	
	TT convert(Object source);

}
