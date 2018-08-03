import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class App {
    /**
     * 建立索引库
     * 修改了============================================
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        //创建文档对象
        Document document = new Document();

        //网文档中添加域字段id
        //StringField：域字段类型
        //特点：不分词，有索引，存储（Store.NO/YES）
        document.add(new StringField("id","111", Field.Store.NO));
        //添加域字段title
        //TextField：域字段类型
        //特点：必须分词，索引，存储（Store.NO/YES）
        document.add(new TextField("title","Lucene从入门到放弃",Field.Store.YES));
        //添加域字段：desc
        document.add(new TextField("desc","Lucene并不是现成的搜索引擎产品，但可以用来制作搜索引擎产品",Field.Store.YES));
        //添加域字段：content
        document.add(new TextField("content","Lucene并不是现成的搜索引擎产品，但可以用来制作搜索引擎产品...", Field.Store.NO));

        Directory directory = FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene"));
//        RAMDirectory ramDirectory = new RAMDirectory();
        //创建分词器对象，把文档对象中的文本进行分词，变成索引单词，单个字为最小分词单元
        //1.基本分词器 特点：一个汉字一个词语
        //StandardAnalyzer analyzer = new StandardAnalyzer();

        //2.二分法分词：	CJKAnalyzer分词器
        //特点：按两个字进行分切
        //CJKAnalyzer analyzer = new CJKAnalyzer();

        //聪明的中国人的分词器 SmartChineseAnalyzer
//        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IKAnalyzer analyzer = new IKAnalyzer();

        //创建核心对象的配置对象：(Lucene版本，使用分词器)
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //创建索引库核心对象indexWriter
        IndexWriter indexWriter = new IndexWriter(directory,config);
//        IndexWriter indexWriter = new IndexWriter(ramDirectory,config);

        //使用indexWriter对象将document对象写入索引库，此过程进行索引创建，并将索引和document对象写入索引库
        indexWriter.addDocument(document);

        //提交
        indexWriter.commit();
        //关闭indexWriter对象
        indexWriter.close();

    }

    /**
     * 基于索引库检索
     */
    @Test
    public void search() throws IOException, ParseException {
        //指定存储索引的磁盘路径
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene")));
//        IndexReader indexReader = DirectoryReader.open(new RAMDirectory());
        //创建搜索索引库的核心对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //指定所搜关键词
        String queryName = "Lucene经典教程";
        //此关键词大于最小分词单元，必须分词才能进行搜索
        //创建查询解析器
        //第二个参数：指定搜索的字段是title
//        QueryParser queryParser = new QueryParser(Version.LUCENE_4_10_3,"title",new StandardAnalyzer());
//        QueryParser queryParser = new QueryParser(Version.LUCENE_4_10_3,"title",new CJKAnalyzer());
//        QueryParser queryParser = new QueryParser(Version.LUCENE_4_10_3,"title",new SmartChineseAnalyzer());
        QueryParser queryParser = new QueryParser(Version.LUCENE_4_10_3,"title",new IKAnalyzer());
        //解析查询关键次，进行分词
        //把分词结果包装到查询对象query对象中
        Query query = queryParser.parse(queryName);

        //根据分词后的query对象查询索引库
        //第二个参数：指定查询结果数量  含义：查询匹配度最高的10条记录，得分越高，匹配度就越高
        //返回结果：文档概要信息
        //1.文档得分
        //2.文档id
        //3.文档命中总记录数
        TopDocs docs = indexSearcher.search(query, 10);

        //获取命中文档总记录
        int totalHits = docs.totalHits;
        System.out.println(totalHits);

        //获取文档得分
        ScoreDoc[] scoreDocs = docs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //获取每一个文档得分
            float score = scoreDoc.score;
            System.out.println("文档得分："+score);

            //获取文档id
            int docId = scoreDoc.doc;
            System.out.println("文档id"+docId);

            //根据文档id获取唯一的文档对象
            Document document = indexSearcher.doc(docId);
            String id = document.get("id");
            System.out.println("文档id："+id);
            String title = document.get("title");
            System.out.println("标题："+title);
            String desc = document.get("desc");
            System.out.println("描述:"+desc);
            String content = document.get("content");
            System.out.println("内容："+content);
        }
    }

}
