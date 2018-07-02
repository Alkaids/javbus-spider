package com.gravel.webmagic.pageprocessor;

import com.gravel.domain.Movies;
import com.gravel.utils.UserAgentUtil;
import com.gravel.webmagic.downloader.MyProxyProvider;
import com.gravel.webmagic.pipeline.IPSpiderPipeline;
import lombok.experimental.var;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gravel on 18/04/13.
 */
@Component
public class JavProxyPoolProcessor implements PageProcessor {

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
            page.putField("result", m);
        }

        for(int i=0;i<2;i++) {
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
        String jsonComment = "";
        final String ip = "127.0.0.1";
        final int port = 1080;
        Map<String,String> headers = new HashMap<>();
        headers.put("accept","*/*");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("referer","https://www.javbus.com/"+avCode);
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("user-agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        headers.put("x-requested-with","XMLHttpRequest");
        Map<String,String> cookies = new HashMap<>();
        cookies.put("__cfduid","d8e8d517716d6f16974aaf77f8821f0901530025670");
        cookies.put("PHPSESSID","vhqldbflrhl8bek4h1jqpo7tk0");
        cookies.put("HstCfa2807330","1530025681968");
        cookies.put("HstCmu2807330","1530025681968");
        cookies.put("starinfo","glyphicon glyphicon-minus");
        cookies.put("HstCnv2807330","5");
        cookies.put("HstCns2807330","12");
        cookies.put("HstCla2807330","1530447264562");
        cookies.put("HstPn2807330","17");
        cookies.put("HstPt2807330","38");
        cookies.put("__cfduid","d8e8d517716d6f16974aaf77f8821f0901530025670");
        cookies.put("PHPSESSID","vhqldbflrhl8bek4h1jqpo7tk0");
        cookies.put("HstCfa2807330","1530025681968");
        cookies.put("HstCmu2807330","1530025681968");
        cookies.put("starinfo","glyphicon glyphicon-minus");
        cookies.put("HstCnv2807330","5");
        cookies.put("HstCns2807330","12");
        cookies.put("HstCla2807330","1530447264562");
        cookies.put("HstPn2807330","17");
        cookies.put("HstPt2807330","38");

 //       accept: */*
//accept-encoding: gzip, deflate, br
//accept-language:
//cookie: __cfduid=d8e8d517716d6f16974aaf77f8821f0901530025670;
// PHPSESSID=vhqldbflrhl8bek4h1jqpo7tk0;
// HstCfa2807330=1530025681968;
// HstCmu2807330=1530025681968;
// starinfo=glyphicon%20glyphicon-minus;
// HstCnv2807330=5; HstCns2807330=12;
// HstCla2807330=1530447264562; HstPn2807330=17;
// HstPt2807330=38;

//referer: https://www.javbus.com/VAGU-193
//user-agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
//x-requested-with: XMLHttpRequest

        https://www.javbus.com/ajax/uncledatoolsbyajax.php?gid=37227545248&lang=zh&img=https://pics.javbus.com/cover/6kgd_b.jpg&uc=0&floor=674
        try {
            jsonComment = Jsoup.
                    connect("https://www.javbus.com/ajax/uncledatoolsbyajax.php?gid=" + gid.trim() + "&lang=zh&img="+img+"uc=0&floor=674")
                    .referrer("https://www.javbus.com/"+avCode)
                    .proxy(ip,port)
                    .get().html().toString();
                 //   .get().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonComment;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void start(JavProxyPoolProcessor processor, IPSpiderPipeline ipPipeline) {
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
}
