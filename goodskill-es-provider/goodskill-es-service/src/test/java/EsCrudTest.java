import com.goodskill.es.EsApplication;
import com.goodskill.es.api.GoodsEsService;
import com.goodskill.es.dto.GoodsDTO;
import com.goodskill.es.model.Goods;
import com.goodskill.es.repository.GoodsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author techa03
 * @date 2020/5/21
 */
@SpringBootTest(classes = EsApplication.class)
@ExtendWith(SpringExtension.class)
@Disabled
public class EsCrudTest {

    @Autowired
    ElasticsearchOperations elasticsearchOperations;
    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    private GoodsEsService goodsEsService;

    @Test
    public void testQuery() {
        Goods entity = new Goods();
        entity.setGoodsId(1002);
        entity.setName("小米12");
        entity.setPrice("1700.00");
        entity.setIntroduce("骁龙888你值得拥有");
        goodsRepository.save(entity);
    }

    @Test
    public void testQuery3() {
        List<GoodsDTO> dtos = goodsEsService.searchWithNameByPage("小米");
        assertTrue(dtos.size() > 0);
    }


}
