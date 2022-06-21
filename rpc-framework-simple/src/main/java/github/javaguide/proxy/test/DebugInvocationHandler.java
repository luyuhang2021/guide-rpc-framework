package github.javaguide.proxy.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/17  5:02 PM
 */
public class DebugInvocationHandler implements InvocationHandler {

    private final Object target;

    public DebugInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before method:"+method.getName());
        Object invoke = method.invoke(target, args);
        System.out.println("after method:"+method.getName());
        return invoke;
    }
}
