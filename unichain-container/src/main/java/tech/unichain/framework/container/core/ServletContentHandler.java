package tech.unichain.framework.container.core;

import tech.unichain.framework.container.request.HttpRequestInputStream;
import tech.unichain.framework.container.request.NettyHttpServletRequest;
import tech.unichain.framework.container.response.NettyHttpServletResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

/**
 * channel激活时， 开启一个新的输入流
 * 有信息/请求进入时，封装请求和响应对象，执行读操作
 * channel恢复时，关闭输入流，等待下一次连接到来
 */
public class ServletContentHandler extends ChannelInboundHandlerAdapter {
    private NettyContext servletContext;
    private HttpRequestInputStream inputStream; // FIXME this feels wonky, need a better approach

    ServletContentHandler(NettyContext servletContext) {
        this.servletContext = servletContext;
    }

    public NettyContext getServletContext() {
        return servletContext;
    }

    public HttpRequestInputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        inputStream = new HttpRequestInputStream(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, false);
            HttpUtil.setKeepAlive(response, HttpUtil.isKeepAlive(request));
            NettyHttpServletResponse servletResponse = new NettyHttpServletResponse(ctx, servletContext, response);
            NettyHttpServletRequest servletRequest = new NettyHttpServletRequest(ctx, this, request, servletResponse);
            servletResponse.setRequest(servletRequest);
            if (HttpUtil.is100ContinueExpected(request)) { //请求头包含Expect: 100-continue
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE), ctx.voidPromise());
            }
            ctx.fireChannelRead(servletRequest);
        }
        if (msg instanceof HttpContent) { //EmptyLastHttpContent, DefaultLastHttpContent
            inputStream.addContent((HttpContent) msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        inputStream.close();
    }
}