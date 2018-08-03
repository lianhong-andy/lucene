import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class App2 {
    /**
     * 创建索引库
     * @throws IOException
     */
    @Test
    public void fuzzyQueryTest() throws IOException {
        Directory directory = FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,new IKAnalyzer());
        //创建索引库核心对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        for (int i = 0; i < 20; i++) {
            //创建文档对象
            Document document = new Document();
            //添加索引字段
            document.add(new LongField("id",i,Field.Store.YES));
            document.add(new TextField("title","Lucene从入门到放弃",Field.Store.YES));
            document.add(new TextField("desc","Lucene是Apache组织提供的一个开源搜索接口，可以用来制作搜索引擎....",Field.Store.YES));
            document.add(new TextField("content","Lucene是Apache组织提供的一个开源搜索接口，可以用来制作搜索引擎....",Field.Store.NO));
            //将索引及文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //提交
        indexWriter.commit();
        //关闭资源
        indexWriter.close();
    }

    /**
     * 基于索引库的检索
     */
    @Test
    public void test2() throws IOException {
        //搜索的关键词
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene")));
        //创建索引库检索的的核心对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建query对象
        FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("title","Lucene"));
        //根据query对象查询索引库
        TopDocs topDocs = indexSearcher.search(fuzzyQuery, 10);
        //检索的总命中数
        int totalHits = topDocs.totalHits;
        System.out.println("文档的总命中数："+totalHits);
        //获取文档得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //每一个文档的得分
            System.out.println("每一个文档的得分:"+scoreDoc.score);

            //获取文档的id
            int docId = scoreDoc.doc;
            System.out.println(docId);

            //根据id获取唯一的文档对对象
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("title"));
            System.out.println(doc.get("desc"));
            System.out.println(doc.get("content"));

        }

    }
    @Test
    public void test3() throws IOException {
        BooleanQuery query = new BooleanQuery();
        NumericRangeQuery numericRangeQuery1 = NumericRangeQuery.newLongRange("id",5L,15L,true,true);
        NumericRangeQuery numericRangeQuery2 = NumericRangeQuery.newLongRange("id",2L,20L,true,true);
        TermQuery termQuery = new TermQuery(new Term("desc","搜索"));
        query.add(numericRangeQuery1, BooleanClause.Occur.MUST_NOT);
        query.add(numericRangeQuery2, BooleanClause.Occur.MUST);
        query.add(termQuery, BooleanClause.Occur.MUST);
        //搜索的关键词
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("E:\\BaiduYunDownload\\Lucene")));
        //创建索引库检索的的核心对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //根据query对象查询索引库
        TopDocs topDocs = indexSearcher.search(query, 10);
        //检索的总命中数
        int totalHits = topDocs.totalHits;
        System.out.println("文档的总命中数：" + totalHits);
        //获取文档得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //每一个文档的得分
            System.out.println("每一个文档的得分:" + scoreDoc.score);

            //获取文档的id
            int docId = scoreDoc.doc;
            System.out.println(docId);

            //根据id获取唯一的文档对对象
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("title"));
            System.out.println(doc.get("desc"));
            System.out.println(doc.get("content"));
        }
    }
}
