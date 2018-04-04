package me.eae.webMagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author ceshi
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/4/414:08
 */
public class MeiZiTuProcessor implements PageProcessor {


    public static final String URL_LIST = "http://www\\.meizitu\\.com/a/more_\\d+\\.html";
    public static final String URL_POST = "http://www\\.meizitu\\.com/a/\\d+.html";
    public static final String URL_IMG = "http://.*\\.jpg";

    private FilePipeline filePipeline = new FilePipeline();
    private Site site = Site
            .me()
            .setDomain("www.meizitu.com")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        //列表页
        if (page.getUrl().regex(URL_LIST).match()) {
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"inWrap\"]").links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
            //文章页
        } else {
            page.putField("images", page.getHtml().xpath("//div[@id=\'picture\']").$("img[src]").regex(URL_IMG).all());
            page.putField("title",page.getHtml().xpath("//div[@class=\'metaRight\']/h2/a").$("a","text"));
        }


    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        MeiZiTuProcessor meiZiTuProcessor = new MeiZiTuProcessor();
        Spider.create(meiZiTuProcessor).addUrl("http://www.meizitu.com/a/more_1.html")
                .addPipeline(new ImgDownLoadPipeline("D://a",meiZiTuProcessor.getSite()))
                .run();
    }
}
