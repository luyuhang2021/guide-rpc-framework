//package github.javaguide.proxy.test;
//
///**
// * @author luyuhang@thinkingdata.cn
// * @date 2022/6/17  3:30 PM
// */
//public class SmsProxy implements SmsService {
//    private final SmsService smsService;
//
//    public SmsProxy(SmsService smsService) {
//        this.smsService = smsService;
//    }
//
//    @Override
//    public String send(String message) {
//        System.out.println("before send");
//        smsService.send(message);
//        System.out.println("after send");
//        return null;
//    }
//
//    @Override
//    public Integer get(int get) {
//        return get;
//    }
//}
