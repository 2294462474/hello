import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AroundProxyTest {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext=
                new ClassPathXmlApplicationContext("application_aroundproxy.xml");
        DoSomeServiceImpl proxyFactory = applicationContext.getBean("proxyFactoryBean", DoSomeServiceImpl.class);
       // Thread.sleep(1000);
       // proxyFactory.doSome();
        proxyFactory.doSome2();
    }
}
