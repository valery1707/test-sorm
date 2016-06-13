package name.valery1707.megatel.sorm;

import javaslang.control.Option;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ReflectionUtils {
	private ReflectionUtils() {
		throw new IllegalStateException();
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getGenericType(Class<?> implementationClazz, Class<?> interfaceClass, String typeId) {
		int typeIndex = -1;
		TypeVariable<? extends Class<?>>[] typeParameters = interfaceClass.getTypeParameters();
		for (int i = 0; i < typeParameters.length && typeIndex < 0; i++) {
			TypeVariable<? extends Class<?>> typeVariable = typeParameters[i];
			if (typeVariable.getName().equals(typeId)) {
				typeIndex = i;
			}
		}
		Assert.state(typeIndex >= 0);//todo Message

		ResolvableType type = ResolvableType.forClass(interfaceClass, implementationClazz);
		return (Class<T>) type.getGeneric(typeIndex).getRawClass();
	}

	@Nonnull
	public static <X, Y> Function<X, Y> findConverter(Class<X> srcClass, Class<Y> dstClass) {
		return Option
				.ofOptional(
						findConverterByConstructor(srcClass, dstClass)
				)
				.orElse(() ->
						Option.ofOptional(findConverterByMethod(srcClass, dstClass))
				)
				.getOrElseThrow(() ->
						new IllegalStateException(String.format("Could not find converter from '%s' to '%s'", srcClass.getName(), dstClass.getName()))
				);
	}

	@SuppressWarnings("unchecked")
	public static <X, Y> Optional<Function<X, Y>> findConverterByConstructor(Class<X> srcClass, Class<Y> dstClass) {
		return Stream
				.of(dstClass.getConstructors())
				.filter(constructor ->
						Modifier.isPublic(constructor.getModifiers()) &&
						constructor.getParameterCount() == 1 &&
						constructor.getParameterTypes()[0].equals(srcClass)
				)
				.findFirst()
				.map(constructor -> src -> {
					try {
						return (Y) constructor.newInstance(src);
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
						throw new IllegalStateException(String.format("Could not convert '%s' to '%s' with '%s(%s)'", srcClass.getName(), dstClass.getName(), constructor.getName(), srcClass.getName()), e);
					}
				});
	}

	@SuppressWarnings("unchecked")
	public static <X, Y> Optional<Function<X, Y>> findConverterByMethod(Class<X> srcClass, Class<Y> dstClass) {
		return Stream
				.of(dstClass.getDeclaredMethods())
				.filter(method ->
						Modifier.isPublic(method.getModifiers()) &&
						Modifier.isStatic(method.getModifiers()) &&
						method.getReturnType().equals(dstClass) &&
						method.getParameterCount() == 1 &&
						method.getParameterTypes()[0].equals(srcClass)
				)
				.findFirst()
				.map(method -> src -> {
					try {
						return (Y) method.invoke(null, src);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new IllegalStateException(String.format("Could not convert '%s' to '%s' with '%s'", srcClass.getName(), dstClass.getName(), method.getName()), e);
					}
				});
	}
}
