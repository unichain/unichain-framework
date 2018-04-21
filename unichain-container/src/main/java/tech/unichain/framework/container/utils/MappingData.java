package tech.unichain.framework.container.utils;

import javax.servlet.Servlet;

/**
 * @author lait.zhang@gmail.com
 * Created on 2017-08-25 12:28.
 */
public class MappingData {

    Servlet servlet = null;
    String servletName;
    String redirectPath ;

    public void recycle() {
        servlet = null;
        servletName = null;
        redirectPath = null;
    }

}
