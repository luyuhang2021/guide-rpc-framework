package github.javaguide.remoting.transport.socket;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/16  4:11 PM
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author shuang.kou
 * @createTime 2020年05月11日 16:56:00
 */
public class MySocketRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(MySocketRpcClient.class);

    public Object send(Message message, String host, int port) {
        //1. 创建Socket对象并且指定服务器的地址和端口号
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //2.通过输出流向服务器端发送请求信息
            objectOutputStream.writeObject(message);
            //3.通过输入流获取服务器响应的信息
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("occur exception:", e);
        }
        return null;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            MySocketRpcClient helloClient = new MySocketRpcClient();
            Message message = (Message) helloClient.send(new Message("content from client"+1), "127.0.0.1", 6666);
            System.out.println("client receive message:" + message.getContent());
        }
    }
}