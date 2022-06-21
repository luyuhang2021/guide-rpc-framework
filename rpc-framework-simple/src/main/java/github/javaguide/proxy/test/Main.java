package github.javaguide.proxy.test;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/17  3:31 PM
 */
public class Main {
    public static void main(String[] args) {
        SmsServiceImpl smsService = new SmsServiceImpl();
//        SmsProxy smsProxy = new SmsProxy(smsService);
//        smsProxy.send("java");
//        System.out.println(smsProxy.get(1));
        interTwo proxy = (interTwo) JdkProxyFactory.getProxy(new SmsServiceImpl());
        proxy.two("1");

    }
}
