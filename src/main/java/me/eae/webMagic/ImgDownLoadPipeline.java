package me.eae.webMagic;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author ceshi
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/4/416:19
 */
public class ImgDownLoadPipeline extends FilePipeline implements Pipeline {

    private Site site;
    HttpClient client;
    public ImgDownLoadPipeline(String path,Site site){
        super.path = path;
        this.site = site;
        HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
        this.client = httpClientGenerator.getClient(site);
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());

        Iterator var3 = resultItems.getAll().entrySet().iterator();
        String title = "";
        if(resultItems.getAll().get("title")!=null){
            title = resultItems.getAll().get("title").toString();
        }

        while(var3.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var3.next();

            String key = entry.getKey();
            if("images".equals(key)){
                List <String> list = (List)entry.getValue();
                for (String url :list) {
                    loadImg(url,title);
                }
            }
        }
    }

    private void loadImg(String url,String filepath){
        HttpGet get = new HttpGet(url);
        HttpResponse response = null;
        InputStream is = null;
        HttpEntity entity = null;
        OutputStream os = null;
        String[] strs = url.split("/");
        String fileName = strs[strs.length-1];
        File file = new File(path+"\\"+filepath);
        if(!file.exists()){
            file.mkdirs();
        }

        try {
            response = client.execute(get);
            entity = response.getEntity();
            is = entity.getContent();
            os = new FileOutputStream(path+"\\"+filepath+"\\"+fileName);
            IOUtils.copy(is, os);
        } catch (Exception e) {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
            e.printStackTrace();
        }


    }
}
