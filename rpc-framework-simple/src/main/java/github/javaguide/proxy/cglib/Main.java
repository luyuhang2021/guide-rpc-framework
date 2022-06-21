package github.javaguide.proxy.cglib;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/20  7:20 PM
 */
public class Main {
    public static void main(String[] args) {
        AliSmsService aliSmsService = (AliSmsService) CglibProxyFactory.getProxy(AliSmsService.class);
        aliSmsService.send("java");
    }
}
