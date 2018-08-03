import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrApp {
    @Test
    public void testCreateIndexSolrInputDocument() throws IOException, SolrServerException {
        //连接solr服务
        String baseURL =  "http://localhost:8080/solr/article";
        HttpSolrServer httpSolrServer = new HttpSolrServer(baseURL);

        //创建SolrInputDocument对象
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id","1");
        doc.addField("title","小米手机");
        doc.addField("content","国产中的骄傲，太牛了");

        //添加索引
        httpSolrServer.add(doc);
        httpSolrServer.commit();
    }

    /**
     * 删除索引
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void test() throws IOException, SolrServerException {
        //连接solr服务
        String baseURL =  "http://localhost:8080/solr/article";
        HttpSolrServer httpSolrServer = new HttpSolrServer(baseURL);


        //删除索引
        httpSolrServer.deleteByQuery("title:手机");
        httpSolrServer.commit();
    }

    @Test
    public void select() throws SolrServerException {
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8080/solr/article");
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        QueryResponse queryResponse = httpSolrServer.query(query);
        SolrDocumentList results = queryResponse.getResults();
        for (SolrDocument result : results) {
            System.out.println(result.get("id"));
            System.out.println(result.get("title"));
            System.out.println(result.get("content"));
        }

    }

    /**
     * 简单查询
     * @throws SolrServerException
     */
    @Test
    public void select2() throws SolrServerException {
        //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/article");

        //创建query对象
        SolrQuery query = new SolrQuery();

        //设置查询条件
        query.setQuery("*:*");

        //执行查询
        QueryResponse queryResponse = solrServer.query(query);
        //获取查询结果
        SolrDocumentList results = queryResponse.getResults();

        //获取查询结果的数量
        System.out.println("记录数:"+results.getNumFound());

        for (SolrDocument result : results) {
            System.out.println(result.get("id"));
            System.out.println(result.get("title"));
            System.out.println(result.get("content"));
        }
    }

    /**
     * 复杂查询
     */
    @Test
    public void queryIndex() throws SolrServerException {
       //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/article");
        //创建Query对象
        SolrQuery query = new SolrQuery();
        //设置查询条件
        query.setQuery("*:*");
        //设置过滤条件
//        query.setFilterQueries("title:手机");
        //设置排序条件(按id倒序)
        query.setSort("id",SolrQuery.ORDER.desc);
        //分页处理
        query.setStart(0);
        query.setRows(10);
        //结果中域的列表
        query.setFields("id","title","content");
        //开启高亮显示
        query.setHighlight(true);
        //高亮显示得域
        query.addHighlightField("title");
        //设置默认搜索域
        query.set("df","article_keywords");
        //高亮显示得前缀
        query.setHighlightSimplePre("<font color='red'>");
        //高亮显示得后缀
        query.setHighlightSimplePost("</font>");

        //执行查询
        QueryResponse queryResponse = solrServer.query(query);

        //获取查询结果集
        SolrDocumentList results = queryResponse.getResults();
        //记录数
        System.out.println("记录数："+results.getNumFound());

        for (SolrDocument result : results) {
            System.out.println(result.get("id"));
            //取高亮显示
            String title = "";
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            List<String> list = highlighting.get("id").get("title");
            //判断是否有高亮内容
            if(list!=null){
                title=list.get(0);
            }else{
                System.out.println("title:"+result.get("title"));
            }
            System.out.println(result.get("content"));
        }

    }
}
