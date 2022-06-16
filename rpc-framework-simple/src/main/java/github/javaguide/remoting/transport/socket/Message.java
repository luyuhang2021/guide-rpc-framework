package github.javaguide.remoting.transport.socket;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/16  4:09 PM
 */
@Data
@AllArgsConstructor
public class Message implements Serializable {
    private String content;
}
