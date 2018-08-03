import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class App1 {
    /**
     * 创建索引库
     */
    @Test
    public void test() throws IOException {
        //创建文档对象
        Document document = new Document();
        //添加索引字段
        document.add(new StringField("id","001",Field.Store.NO));
        document.add(new StringField("title","Lucene从入门到放弃",Field.Store.YES));
        document.add(new TextField("desc","Lucene是Apache组织提供的一个开源搜索接口，可以用来制作搜索引擎....",Field.Store.YES));
        document.add(new TextField("content","Lucene是Apache组织提供的一个开源搜索接口，可以用来制作搜索引擎....",Field.Store.NO));
        //指定索引库的存储位置
        Directory directory = FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene"));
        //创建分词器
        IKAnalyzer analyzer = new IKAnalyzer();
        //创建索引库核心对象的配置对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //创建索引库核心对象
        IndexWriter indexWriter = new IndexWriter(directory,config);

        /**
         * 将索引及文档对象下写入索引库
         */
        indexWriter.addDocument(document);
        //提交
        indexWriter.commit();
        //关闭资源
        indexWriter.close();
    }

    /**
     * 基于索引库的检索
     */
    @Test
    public void test2() throws IOException, ParseException {
        //指定索引库的磁盘路径
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene")));
        //创建索引库搜索的核心对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //指定搜索关键词
        String queryName = "标题";

        //创建查询解析器
        QueryParser parser = new QueryParser(Version.LUCENE_4_10_3,"title",new IKAnalyzer());
        //指定多个字段检索并分词
//        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title","desc"}, new IKAnalyzer());
        //对搜索关键词进行分词，并将分词结果存人query对象中
        Query query = parser.parse(queryName);

        FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("title", "Lucene"));

        //TermQuery 根据词条进行检索，不会切词了，因为词条已是最小单位
        TermQuery termQuery = new TermQuery(new Term("title",queryName));

        //WildcardQuery 模糊检索
        Term term = new Term("title", "*"+queryName + "*");
        WildcardQuery wildcardQuery = new WildcardQuery(term);

        //查询所有
        MatchAllDocsQuery matchAllDocsQuery = new MatchAllDocsQuery();
        System.out.println(matchAllDocsQuery);

        //根据分词后的query对象查询索引库
        TopDocs docs = indexSearcher.search(query, 10);//10表示查询匹配度最高的10条记录

        //获取命中文档的总记录数
        int totalHits = docs.totalHits;
        System.out.println("命中文档的记录数"+totalHits);

        //获取文档得分
        ScoreDoc[] scoreDocs = docs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            float score = scoreDoc.score;
            System.out.println("每一个文档的的分："+score);

            //获取文档的id
            int docId = scoreDoc.doc;
            System.out.println("文档id: "+docId);

            //根据文档id获取唯一的文档对象
            Document document = indexSearcher.doc(docId);

            String id = document.get("id");
            String title = document.get("title");
            String desc = document.get("desc");
            String content = document.get("content");
            System.out.println("id:"+id);
            System.out.println("title:"+title);
            System.out.println("desc:"+desc);
            System.out.println("content:"+content);
        }
    }

    @Test
    public void testMultiFieldQueryParser() throws ParseException {
        //	MultiFieldQueryParser
        //作用：指定多个字段检索并分词
        String queryName = "Lucene从入门到热爱";
        IKAnalyzer analyzer = new IKAnalyzer();
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title","desc"},analyzer);
        Query query = parser.parse(queryName);
        System.out.println(query);
    }



    /**
     * 删除指定索引
     */
    @Test
    public void testIndexDelete() throws IOException {
        //创建directory
        Directory directory = FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene"));
        //创建索引库核心对象的配置对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,new IKAnalyzer());
        //创建索引库的核心对象
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //根据Term删除索引库
        indexWriter.deleteDocuments(new Term("desc","引擎"));
        //释放资源
        indexWriter.close();
    }

    /**
     * 删除全部索引（不可恢复）
     */@Test
    public void testIndexDeleteAll() throws IOException {
        //创建directory
        Directory directory = FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene"));
        //创建索引库核心对象的配置对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,null);
        //创建索引库的核心对象
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //根据Term删除索引库
        indexWriter.deleteAll();
        //释放资源
        indexWriter.close();
    }

    /**
     * 修改索引
     * @throws Exception
     */
    @Test
    public void testIndexUpdate() throws Exception {
        // 创建Directory流对象
        Directory directory = FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, new IKAnalyzer());
        // 创建写入对象
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // 创建Document
        Document document = new Document();
        document.add(new TextField("c", "c", Field.Store.YES));
        document.add(new TextField("b", "b", Field.Store.YES));

        // 执行更新，会把所有符合条件的Document删除，再新增。
        indexWriter.updateDocument(new Term("b", "b"), document);

        // 释放资源
        indexWriter.close();
    }

    /**
     * 改变boost值来改变文档得分
     * 从而实现文档排名
     */
    @Test
    public void rank() throws IOException {
        Directory directory = FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory,config);

        for (int i = 0; i < 10; i++) {
            Document document = new Document();
            document.add(new StringField("id",i+"",Field.Store.YES));
            TextField title = new TextField("title","标题"+i,Field.Store.YES);

            if(i==8){
                title.setBoost(1000f);
            }
            document.add(title);
            document.add(new TextField("content","内容"+i,Field.Store.YES));

            indexWriter.addDocument(document);

        }

        indexWriter.commit();
        indexWriter.close();
    }
}
