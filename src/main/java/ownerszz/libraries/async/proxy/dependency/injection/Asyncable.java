package ownerszz.libraries.async.proxy.dependency.injection;
import ownerszz.libraries.dependency.injection.core.Dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Dependency
public @interface Asyncable {
}
