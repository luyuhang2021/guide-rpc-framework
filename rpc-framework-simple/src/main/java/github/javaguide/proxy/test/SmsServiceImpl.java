package github.javaguide.proxy.test;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/17  3:29 PM
 */
public class SmsServiceImpl implements SmsService,interTwo {
    @Override
    public String send(String message) {
        System.out.println("send:" + message);
        return message;
    }

    @Override
    public Integer get(int get) {
        return get;
    }


    @Override
    public String two(String two) {
        System.out.println("2");
        return two;
    }
}
