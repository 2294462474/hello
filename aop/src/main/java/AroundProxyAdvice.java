import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AroundProxyAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("前置===============");
        //用于启动目标方法执行的
        Object proceed = invocation.proceed();
        System.out.println("后置===============");
        return  proceed;
    }
}
