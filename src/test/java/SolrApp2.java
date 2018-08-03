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

public class SolrApp2 {
    /**
     * 简单查询
     * @throws SolrServerException
     */
    @Test
    public void test() throws SolrServerException {
        //创建连接对象
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8080/solr/products");
        //创建查询对象
        SolrQuery query = new SolrQuery();
        //设置查询条件
        query.setQuery("*:*");
        //执行查询
        QueryResponse queryResponse = httpSolrServer.query(query);
        //获取查询结果集
        SolrDocumentList results = queryResponse.getResults();
        //命中总记录数
        System.out.println("命中总记录数:"+results.getNumFound());
        for (SolrDocument result : results) {
            System.out.println(result.get("id"));
            System.out.println(result.get("product_catalog_name"));
            System.out.println(result.get("product_catalog"));
            System.out.println(result.get("product_price"));
            System.out.println(result.get("product_name"));
            System.out.println(result.get("product_picture"));
            System.out.println(result.get("_version_"));
        }
    }

    /**
     * 复杂查询
     */
    @Test
    public void test2() throws SolrServerException {

        //创建query对象，封装查询条件
        SolrQuery query = new SolrQuery();
        //1.主查询条件
        query.setQuery("牙膏");
        //2.fq 过滤条件（filter query）
        //a.查询商品类别是时尚卫浴
        query.addFilterQuery("product_catalog_name:时尚卫浴");
        //b.商品价格在1-20
        query.addFilterQuery("product_price:[1 TO 20]");
        //设置排序
        //第一个参数：指定对哪个域进行排序
        //第二个参数：升序，降序
        query.setSort("product_price",SolrQuery.ORDER.asc);

        //分页
        query.setStart(1);
        query.setRows(6);

        //设置过滤字段
        //查询需要过滤显示的字段，字段之间使用空格或者逗号隔开
//        query.setFields("product_name,product_price");

        //设置高亮显示
        //开启高亮显示
        query.setHighlight(true);
        //指定高亮显示字段
        query.addHighlightField("product_name");
        //设置高亮显示得前缀
        query.setHighlightSimplePre("<font color='red'>");
        //设置高亮显示的后缀
        query.setHighlightSimplePost("</font>");
        //设置默认查询字段
        query.set("df","product_keywords");

        //执行
        this.executeAndPrintResult(query);
    }

    private void executeAndPrintResult(SolrQuery query) throws SolrServerException {
        //创建连接对象
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/products");
        //执行查询
        QueryResponse queryResponse = solrServer.query(query);
        //获取查询结果集
        SolrDocumentList results = queryResponse.getResults();

        //命中总记录数
        System.out.println("命中总记录数:"+results.getNumFound());
        for (SolrDocument result : results) {
            System.out.println(result.get("id"));
            String id = (String)result.get("id");
            String product_name = (String)result.get("product_name");
            //获取高亮
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            Map<String, List<String>> map = highlighting.get(id);
            List<String> list = map.get("product_name");
            if(list!=null&&list.size()>0){
                product_name = list.get(0);
            }
            System.out.println(product_name);
            System.out.println(result.get("product_catalog_name"));
            System.out.println(result.get("product_catalog"));
            System.out.println("price:"+result.get("product_price"));
            System.out.println(result.get("product_name"));
            System.out.println(result.get("product_picture"));
            System.out.println(result.get("_version_"));
        }

    }
}
