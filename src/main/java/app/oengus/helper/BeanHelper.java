package app.oengus.helper;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.annotation.Nullable;
import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Helper class to copy properties while ignoring null properties
 */
public class BeanHelper {

	public static String[] getNullPropertyNames(final Object source) {
		final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
		return Stream.of(wrappedSource.getPropertyDescriptors())
		             .map(FeatureDescriptor::getName)
		             .filter(
		                 // remove null values
		                 (propertyName) -> wrappedSource.getPropertyValue(propertyName) == null &&
                             // but only if they don't have the javax.annotation.Nullable annotation
                             !wrappedSource.getPropertyTypeDescriptor(propertyName).hasAnnotation(Nullable.class)
                     )
		             .toArray(String[]::new);
	}

    /**
     * Copy properties from {@code src} to {@code target} while ignoring {@code null} properties in {@code src}
     *
     * @param src The source bean
     * @param target The target bean, the properties from src will be copied to this
     */
	public static void copyProperties(final Object src, final Object target) {
		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	public static void copyProperties(final Object src, final Object target, final String... ignoreProperties) {
		BeanUtils.copyProperties(src, target, ArrayUtils.addAll(getNullPropertyNames(src), ignoreProperties));
	}

}
