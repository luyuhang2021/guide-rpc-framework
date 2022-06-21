package github.javaguide.proxy.cglib;

public class AliSmsService {
    public String send(String message) {
        System.out.println(getClass().getName());
        System.out.println("send message:" + message);
        return message;
    }
}