import com.offcn.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author CX
 * @Date 2020/2/12 20:51
 * @Version 1.0
 * @Description：
 * solr也相当于一个数据库，将数据存储solr数据库中，然后进行搜索，
 * 搜索时他会根据中文的常用关键词进行搜索
 * 增删改要进行事务的提交（commit）
 *
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-solr.xml")
public class TestTemplate {

    /*操作solr的对象*/
    @Autowired
    private SolrTemplate solrTemplate;

    /*
        测试向solr数据库中的添加
        相当于数据库的添加
        单个对象的添加
    */
    @Test
    public void testAdd(){
        TbItem item=new TbItem();

        item.setId(3L);
        item.setBrand("小米");
        item.setCategory("手机pluse");
        item.setGoodsId(1L);
        item.setSeller("小米1号专卖店");
        item.setTitle("红米Mate9");
        item.setPrice(new BigDecimal(2200));

        solrTemplate.saveBean(item);  //调用添加方法进行添加
        solrTemplate.commit();  //事务的提交
    }

    /*
    * 主键查询
    * */
    @Test
    public void testFindOne(){
        //使用对象调用查询方法，参数1：查询条件，参数2：传入要查询的实体的字节码文件对象
        TbItem item = solrTemplate.getById("3", TbItem.class);
        System.out.println(item.getTitle());
    }
    /*
    * 主键删除
    * */
    @Test
    public void testDelete(){
        solrTemplate.deleteById("3");
        solrTemplate.commit();
    }


    /*
    * 分页查询+批量添加（直接添加一个存啦多个对象的集合）
    * */
    @Test
    public void testAddList(){
        List<TbItem> itemList=new ArrayList<>();

        for (int i=0;i<100;i++){
            TbItem item=new TbItem();

            item.setId(Long.valueOf(i));
            item.setBrand("小米");
            item.setCategory("手机pluse");
            item.setGoodsId(1L);
            item.setSeller("小米1号专卖店");
            item.setTitle("红米Mate9");
            item.setPrice(new BigDecimal(2200));

            itemList.add(item);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }
    @Test
    public void testPageQuery(){
        //创建查询对象：*:*  是对所有的数据进行操作
        Query query=new SimpleQuery("*:*");
        query.setOffset(0);  //分页开始的索引
        query.setRows(10);   //分页每页的数据量
        //得到分页对象，这里面除啦数据外，还有分页所需的内容：传入的是分页的条件，和TbItem的字节码文件对象
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        //从分页对象中取出数据，此数据是分页后的数据展示
        List<TbItem> list = page.getContent();
        for (TbItem item:list){
            System.out.println(item.getTitle());
        }
    }


    /*
    * 条件查询
    * 将查询的条件封装进Criteria对象
    * */
    @Test
    public void testPageQueryMutil(){
        //查询条件对象
        Query query=new SimpleQuery("*:*");
        //封装查询条件的条件对象
        //条件为item_title字段值中含有9
        Criteria criteria=new Criteria("item_title").contains("9");

        //为查询对象中添加条件对象
        query.addCriteria(criteria);

        //排序对象
        Sort sort=new Sort(Sort.Direction.DESC,"item_price");
        query.addSort(sort);

        //加啦查询条件，进行分页查询
        query.setOffset(0);
        query.setRows(10);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = page.getContent();
        for (TbItem item:content){

            System.out.println(item.getTitle());
        }
    }

    /*
    * 全部删除
    *
    * */
    public void testDeleteAll(){
        Query query =new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
