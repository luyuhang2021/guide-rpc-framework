package github.javaguide.proxy.test;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/17  3:27 PM
 */
public interface SmsService {
    /**
     * send message
     * @param message
     * @return message
     */
    String send(String message);

    Integer get(int get);

}
