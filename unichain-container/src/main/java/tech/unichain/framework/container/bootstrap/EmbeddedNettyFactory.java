package tech.unichain.framework.container.bootstrap;

import tech.unichain.framework.container.core.NettyContainer;
import tech.unichain.framework.container.core.NettyContext;
import io.netty.bootstrap.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.AbstractEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import javax.servlet.ServletException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;

/**
 * Spring Boot会查找EmbeddedServletContainerFactory接口的实现类(工厂类)，调用其getEmbeddedServletContainer()方法，来获取web应用的容器
 * 所以我们要实现这个接口，这里不直接实现，而是通过继承AbstractEmbeddedServletContainerFactory类来实现
 *
 * @author lait.zhang@gmail.com on 2017-08-24.
 */
public class EmbeddedNettyFactory extends AbstractEmbeddedServletContainerFactory implements ResourceLoaderAware {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String SERVER_INFO = "Netty@SpringBoot";
    private ResourceLoader resourceLoader;

    @Override
    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
        ClassLoader parentClassLoader = resourceLoader != null ? resourceLoader.getClassLoader() : ClassUtils.getDefaultClassLoader();
        //Netty启动环境相关信息
        Package nettyPackage = Bootstrap.class.getPackage();
        String title = nettyPackage.getImplementationTitle();
        String version = nettyPackage.getImplementationVersion();
        log.info("Running with " + title + " " + version);
        //是否支持默认Servlet
        if (isRegisterDefaultServlet()) {
            log.warn("This container does not support a default servlet");
        }
        //上下文
        NettyContext context = new NettyContext(getContextPath(), new URLClassLoader(new URL[]{}, parentClassLoader), SERVER_INFO);
        for (ServletContextInitializer initializer : initializers) {
            try {
                initializer.onStartup(context);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        //从SpringBoot配置中获取端口，如果没有则随机生成
        int port = getPort() > 0 ? getPort() : new Random().nextInt(65535 - 1024) + 1024;
        InetSocketAddress address = new InetSocketAddress(port);
        log.info("Server initialized with port: " + port);
        return new NettyContainer(address, context); //初始化容器并返回
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
