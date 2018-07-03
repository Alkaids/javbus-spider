package com.gravel.webmagic.pageprocessor;

import com.gravel.domain.Movies;
import com.gravel.utils.UserAgentUtil;
import com.gravel.webmagic.pipeline.JvaSpiderPipeline;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gravel on 18/04/13.
 */
@Component
public class JavProxyPoolProcessor implements PageProcessor {
    private final static String authHeader = authHeader("ZF201841982132k9HuP", "1b08e7d255d6412bb8b539e8196a9d2f", (int) (new Date().getTime()/1000));

    private Site site = Site.me().setDisableCookieManagement(true)
            .setTimeOut(6000).setRetryTimes(3)
            .setSleepTime(1000)
            .setCharset("UTF-8")
            .addHeader("Accept-Encoding", "/")
            .setUserAgent(UserAgentUtil.getRandomUserAgent());

    @Override
    public void process(Page page) {
        List<String> targetUrl = page.getHtml().xpath("//a[@class='movie-box']/@href").all();
        if(page.getUrl().toString().contains("page")&&targetUrl!=null){
            for(String url : targetUrl){
                page.addTargetRequest(url);
            }
        }
        Movies m = new Movies();
        String picUrl = page.getHtml().xpath("//a[@class='bigImage']/@href").toString();
        if(picUrl!=null){
            List<Selectable> ele = page.getHtml().xpath("//div[@class='col-md-3 info']/").nodes();
            String avCode = ele.get(0).xpath("//span").nodes().get(1).xpath("//span/text()").toString();
            String publishDate = ele.get(1).xpath("//p/text()").toString();
            String footage = ele.get(2).xpath("//p/text()").toString();
            String director = ele.get(3).xpath("//p/a/text()").toString();
            String manufacturer = ele.get(4).xpath("//p/a/text()").toString();
            String publisher = ele.get(5).xpath("//p/a/text()").toString();

            String series = ele.get(6).xpath("//p/a/text()").toString();
            List<Selectable> categoriesList = page.getHtml().xpath("//span[@class='genre']/").nodes();
            StringBuffer categories  = new StringBuffer();
            for(int i = 0;i<categoriesList.size();i++){
                categories.append(categoriesList.get(i).xpath("//a/text()").toString())
                        .append(",");
            }
            String gid  = page.getHtml().xpath("//script").nodes().get(8).toString().trim().split(";")[0].split("=")[1];
            String downloadUrl = crawlDownloadUrl(gid,picUrl,avCode);
            m.setAvCode(avCode);
            m.setPublishDate(publishDate);
            m.setFootage(footage);
            m.setDirector(director);
            m.setPublisher(publisher);
            m.setManufacturer(manufacturer);
            m.setSeries(series);
            m.setCategories(categories.substring(0,categories.length()-1).toString());
            m.setDownloadUrl(downloadUrl);
            m.setPicUrl(picUrl);
            if(!StringUtils.isEmpty(downloadUrl)){
                page.putField("result", m);
            }
        }

        for(int i=0;i<71;i++) {
            page.addTargetRequest("http://www.javbus.com/page/"+(i+1)+"/");
        }
    }

    /**
     * 爬去下载链接
     * @param gid
     * @param img
     * @return
     */
    public static String crawlDownloadUrl(String gid,String img,String avCode) {
        String targetUrl = "";
        String downloadUrl = "";
        final String ip = "forward.xdaili.cn";//这里以正式服务器ip地址为准
        final int port = 80;//这里以正式服务器端口地址为准
        try {
            targetUrl = Jsoup.
                    connect("http://alicili.cc/list/"+avCode+"/1-0-0/")
                    .userAgent(UserAgentUtil.getRandomUserAgent())
                    .proxy(ip,port)
                    .header("Proxy-Authorization", authHeader)
                    .get().body().getElementsByClass("item")
                    .first()
                    .getElementsByTag("a").first().attr("href");

            downloadUrl  = Jsoup.
                    connect(targetUrl)
                    .userAgent(UserAgentUtil.getRandomUserAgent())
                    .proxy(ip,port)
                    .header("Proxy-Authorization", authHeader)
                    .get()
                    .body()
                    .getElementsByClass("dd magnet")
                    .first()
                    .getElementsByTag("a")
                    .first().attr("href");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return downloadUrl;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void start(JavProxyPoolProcessor processor, JvaSpiderPipeline ipPipeline) {
        final String ip = "127.0.0.1";
        final int port = 1080;
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(ip,port)));

        Spider.create(processor)
                .addUrl("http://www.javbus.com/")
                .thread(5)
                .setDownloader(httpClientDownloader)
                .addPipeline(ipPipeline)
                .run();
    }

    /**
     * http://www.xdaili.cn/usercenter/order
     * 讯代理 买了10W 的
     * @param orderno
     * @param secret
     * @param timestamp
     * @return
     */

    public static String authHeader(String orderno, String secret, int timestamp){
        //拼装签名字符串
        String planText = String.format("orderno=%s,secret=%s,timestamp=%d", orderno, secret, timestamp);

        //计算签名
        String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(planText).toUpperCase();

        //拼装请求头Proxy-Authorization的值
        String authHeader = String.format("sign=%s&orderno=%s&timestamp=%d", sign, orderno, timestamp);
        return authHeader;
    }
}
