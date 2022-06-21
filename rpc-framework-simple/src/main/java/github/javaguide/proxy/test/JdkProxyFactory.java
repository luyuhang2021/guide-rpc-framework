package github.javaguide.proxy.test;

import java.lang.reflect.Proxy;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/17  5:06 PM
 */
public class JdkProxyFactory {
    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new DebugInvocationHandler(target)
        );
    }
}
