//import com.goodskill.es.EsApplication;
//import com.goodskill.es.api.GoodsEsService;
//import com.goodskill.es.model.Goods;
//import com.goodskill.es.repository.GoodsRepository;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.TermQueryBuilder;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.SearchHits;
//import org.springframework.data.elasticsearch.core.query.Criteria;
//import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
//import org.springframework.data.elasticsearch.core.query.Query;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
///**
// * @author techa03
// * @date 2020/5/21
// */
//@SpringBootTest(classes = EsApplication.class)
//@ExtendWith(SpringExtension.class)
//@Disabled
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
//            Goods goods1 = new Goods();
//            BeanUtils.copyProperties(n, goods1);
//            goodsRepository.save(goods1);
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
//        goodsEsService.searchWithNameByPage("test").forEach(System.out::println);
//    }
//
//
//}
