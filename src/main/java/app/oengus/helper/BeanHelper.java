package app.oengus.helper;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

public class BeanHelper {

	public static String[] getNullPropertyNames(final Object source) {
		final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
		return Stream.of(wrappedSource.getPropertyDescriptors())
		             .map(FeatureDescriptor::getName)
		             .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
		             .toArray(String[]::new);
	}

	public static void copyProperties(final Object src, final Object target) {
		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	public static void copyProperties(final Object src, final Object target, final String... ignoreProperties) {
		BeanUtils.copyProperties(src, target, ArrayUtils.addAll(getNullPropertyNames(src), ignoreProperties));
	}

}
