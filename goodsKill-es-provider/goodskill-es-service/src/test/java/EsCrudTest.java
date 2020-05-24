//import com.goodskill.es.EsApplication;
//import com.goodskill.es.api.GoodsEsService;
//import com.goodskill.es.model.Goods;
//import com.goodskill.es.model.Goods1;
//import com.goodskill.es.repository.GoodsRepository;
//import com.goodskill.es.repository.GoodsRepository1;
//import org.elasticsearch.index.query.IdsQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.TermQueryBuilder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//import org.springframework.data.elasticsearch.core.SearchHit;
//import org.springframework.data.elasticsearch.core.SearchHits;
//import org.springframework.data.elasticsearch.core.query.*;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
///**
// * @author techa03
// * @date 2020/5/21
// */
//@SpringBootTest(classes = EsApplication.class)
//@RunWith(SpringRunner.class)
//public class EsCrudTest {
//
//    @Autowired
//    ElasticsearchOperations elasticsearchOperations;
//    @Autowired
//    GoodsRepository goodsRepository;
//    @Autowired
//    private GoodsEsService goodsEsService;
//    @Value("${dubbo.registry.address}")
//    private String address;
//
//    @Test
//    public void testQuery() {
//        goodsRepository.findAll().forEach(n -> {
//            Goods1 goods1 = new Goods1();
//            BeanUtils.copyProperties(n, goods1);
//            goodsRepository1.save(goods1);
//        });
//    }
//
//
//    @Test
//    public void testQuery1() {
//        SearchHits<Goods> goods = elasticsearchOperations.search(Query.findAll(), Goods.class);
////        goods.getSearchHits().stream().map(SearchHit::getContent).forEach(System.out::println);
//        Criteria contains = new Criteria("name").contains("小米4");
//        QueryBuilder query = new TermQueryBuilder("name", "小米");
//        elasticsearchOperations.search(new NativeSearchQuery(query), Goods.class)
//        .getSearchHits().forEach(System.out::println);
//    }
//
//    @Test
//    public void testQuery2() {
//        goodsEsService.searchWithNameByPage("").forEach(System.out::println);
//    }
//
//
//}
