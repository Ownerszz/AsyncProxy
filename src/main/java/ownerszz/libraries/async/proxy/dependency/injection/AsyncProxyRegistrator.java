package ownerszz.libraries.async.proxy.dependency.injection;

import ownerszz.libraries.async.proxy.core.AsyncProxyFactory;
import ownerszz.libraries.dependency.injection.core.DependencyManager;
import ownerszz.libraries.dependency.injection.core.DependencyRegistrator;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;

@DependencyRegistrator
public class AsyncProxyRegistrator {
    @ResolveDependencies
    public AsyncProxyRegistrator(DependencyManager dependencyManager) throws Exception {
        dependencyManager.registerPoxyOnAnnotation(Asyncable.class, impl -> {
            try {
                return AsyncProxyFactory.createProxy(impl);
            }catch (Exception exception){
                throw new RuntimeException(exception);
            }
        });
    }
}
