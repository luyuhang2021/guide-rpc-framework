package github.javaguide.remoting.transport.netty.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class MyRpcRequest {
    private String interfaceName;
    private String methodName;
}