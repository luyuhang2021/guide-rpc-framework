package github.javaguide.remoting.transport.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/16  3:53 PM
 */

public class MySocketRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(MySocketRpcServer.class);
    private ThreadPoolExecutor threadPoolExecutor;

    public MySocketRpcServer() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPoolExecutor = new ThreadPoolExecutor(10, 100, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), threadFactory);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                Socket finalSocket = socket;
                threadPoolExecutor.execute(()->{
                    logger.info("Server accept");
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(finalSocket.getInputStream());
                         ObjectOutputStream objectOutputStream = new ObjectOutputStream(finalSocket.getOutputStream())
                    ) {
                        Message message = (Message) objectInputStream.readObject();
                        logger.info("message:"+message.getContent());
                        message.setContent("ok");
                        objectOutputStream.writeObject(message);
                        objectOutputStream.flush();
                    } catch (ClassNotFoundException | IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        MySocketRpcServer mySocketRpcServer = new MySocketRpcServer();
        mySocketRpcServer.start(6666);
    }
}
