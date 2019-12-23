package org.socmap.slide.utils;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;

public class SlideProxy {

    public static Proxy getSeleniumProxy() {
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);
        // 禁用广告等连接
        proxy.blacklistRequests(".*google*.*", 404);
        proxy.blacklistRequests("trace2.rtbasia.*", 404);
        proxy.blacklistRequests("xvb.rtbasia.*", 404);
        proxy.blacklistRequests("https://trace2.rtbasia.com/rtbasia_viewability.min.js", 404);
        proxy.blacklistRequests("https://trace2.rtbasia.com/mstr", 404);
        // 创建selenium代理对象
        return ClientUtil.createSeleniumProxy(proxy);
    }
}