package org.seckill.service.common.trade.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import org.apache.commons.lang.StringUtils;
import org.seckill.api.exception.SeckillException;
import org.seckill.dao.GoodsMapper;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.seckill.service.common.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyangkly on 15/8/9.
 * 简单main函数，用于测试当面付api
 * sdk和demo的意见和问题反馈请联系：liuyang.kly@alipay.com
 */
@Component
public class AlipayRunner {
    private static Logger logger = LoggerFactory.getLogger(AlipayRunner.class);
    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;
    @Value("${QRCODE_IMAGE_DIR}")
    private String QRCODE_IMAGE_DIR;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo-test.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

    }

    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsMapper goodsDao;

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    // 测试当面付2.0生成支付二维码
    public String trade_precreate(long seckillId) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "xxx数码商城";
        Seckill seckill = redisService.getSeckill(seckillId);
        Goods goods = goodsDao.selectByPrimaryKey(seckill.getGoodsId());
        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = String.valueOf(seckill.getPrice());

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuffer("购买商品1件共").append(seckill.getPrice()) + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088102177423861");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail

        GoodsDetail goods1 = GoodsDetail.newInstance(String.valueOf(seckill.getGoodsId()), goods.getName(), seckill.getPrice().intValue() * 100L, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

        AlipayClient alipayClient = new
                DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do", "2016092600603960",
                "MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQCCELbaf3KKTlPGcTyZCjP+jGj63QdV+lQBWdedM0qkPsqMR2363GA3kL3VH8UpdlzjIw7c4QHsfENiRK3kyheAeeHUkccQ6R759rPOp/UZ9X22ZkQnIL2h/TU4Kyp7WuyUnhJsUAj+aQO6xjQ6h212PLPoGWb/FhdppP3/0C1j/CrOqb3KW/nUJrFc/ttMuVmNTvtjyyuAakzaPAaCAO4mk+1P4Jqk5rTkDkVD5Wi5qeYZ7rhHZVN0KYzyZp/FtyuW8adrb8a8A1QZjv5js44H/0w3z0o/4sqgglQxr6PdUghXKAo2J4BT4ctOSD4qj/1JqltuFXIhKZ2pLkubezH7AgMBAAECgf9ElovWrV65kJSdERnjmn3QvjqeD/DOZjPmgnoxa9rwwiwNzZ0xrQUQSEPTH+3Y75IMNXChCgb5ro3aRF1vSSMbnSp57mYLhOPQ7Ufksq2OAudISKWn/JwD0INAG4NLiac4uCL++zXC854D4IGqW8BdmaVFwB+QZRXzoA5R2HYXQyK9QGm3fKSB94ZuNJzYjmC3U3K7VbQ3QSGCb2+NpULrS6qFf8VBif0vraVopjqUBxEfw9cpkbzhh+MlaM0dG9TKvhvBoTMy0F5H9Ez+Mo+6eVoO7ORgBRQnW7ejdQ43yk8JtDGLMS3k4ZIPPafPeAZotjjBDcRYw5d7KgVF3tkCgYEAvQZapU0bxSGgP4a1gBLXmWtckIOsXPiUbQfMsmdVHLpo+z8/tOx1iXDQhJVs2+QDteuGxVW+luVVc+Urx7x8MgH5Rq6qw46WXjXi4nFNPDf94/WjDlz9oqTAfwhDL14FyAXTbZZjkuaneCfbvzfTdMzCS/VJ6jnH4pz7IBQAZt0CgYEAsCZjqpBnWLy/RXf7hr5NakhvQvGZPV4yIqPZn/pHqm0CA3qCALulzSgFLoBmRj11jrVco2/tFrnvFd8n+xLh9BTUiYJ7I72EiZ7LPi4i5d02WfmWhUWraCDaup6pt3vMM16UpaLTn4Pea+oZmWYsugXwWIs8UgxM/PQCz62csrcCgYB1TWInPsjC9vEZOyGbTxYYkTWqQOxP7bs+Rh7Tf6ij4VKQyFlij3MJTcc43/nkI4Xh+T1zbJiPz9XNzCP1GZUA+5SUNoSmQvAxmsMnvyQtAJpAQwljOnXz3sXOj3QSCwkPbf4c/bzhRt4ahLGr4psI2qCgvqGWBzFyAfUdnQv6yQKBgE5qmJ3asRcw11leo2uFlkMFQfqkQRqvr1P6aywrp/G3m1jfFbar0pC1CzF7WYxVWdsQ48+iSQJ//W5YLJ0597rH9yHMI3Nyr+jv9vyUX1bDFqoRUS+9JyQw1A6WCWm0Q+Bub3AZP2pYsxCJY6IFeU3KzQct3MmNzkwheIrpd8ObAoGBAKaUrS6pfgFpGrnrIn0v2pcfIUPSLrsBPoetHlzoeZ1optQ2pebu8j0nva6wQyq9iIuYc/xQhzm2TSuWLvHuxGpmzbmMkMOnnYVB/3C6ugbGUXzwoWfWIE2+U9oPQb1s9bSIqV8d1AcIg8LlLAZE7gPVtAIwiwYtGq6En+9z0ZsF",
                "json", "utf-8",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw8lDRYz3RcEwFzEjMC/wuI25NsLk/HTggwRNqfGfao/11qvU9IcGmdz4esE7+sscCGsV84vb42OVjGogw/qv82XvmqsL11b7dlbiGLJ9c9Hj+EBb/G/1JsNclwiYc1QJX9t6xf2elIipAkDfDSiTyMsO0PY0ZCxKmo1OJe5JGY2vjfrPX8I2J+YGRHQEN4WoCtYVImGPqtH/gukIZ9BSsxI6mwLlD0nZhl36gXlSYd1dZc4/ugoGGZsQUR9hMewwBSAMDxg4/84irMW01ZjzbIJ6i8hKN2YqrZ/kVoiTlpwHPQgoGyZedeqlyUddP9ULGMHhaUONNNog5zGmYLegIwIDAQAB", "RSA2");

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePayModel model = new AlipayTradePayModel();
        request.setBizModel(model);

        // 创建扫码支付请求builder，设置请求参数
        model.setSubject(subject);
        model.setTotalAmount(totalAmount);
        model.setOutTradeNo(outTradeNo);
        model.setUndiscountableAmount(undiscountableAmount);
        model.setSellerId(sellerId);
        model.setBody(body);
        model.setOperatorId(operatorId);
        model.setStoreId(storeId);
        model.setTimeoutExpress(timeoutExpress);
        //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置

    //    model.setGoodsDetail(goodsDetailList);
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);
            dumpResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("支付宝预下单成功: )");

        String filePath = "";
        // 需要修改为运行机器上的路径
        File file = new File(QRCODE_IMAGE_DIR);
        if (!file.exists()) {
            if (file.mkdirs() && file.exists()) {
                logger.info("二维码存放目录创建成功，请检查！");
            } else if (!file.exists()) {
                logger.error("创建目录失败，请检查用户权限！");
                throw new SeckillException("创建目录失败，请检查用户权限！");
            }
        }
        int filePathDeepth = 0;
        if (response != null && StringUtils.isNotEmpty(response.getOutTradeNo())) {
            filePath = String.format(QRCODE_IMAGE_DIR + "/qr-%s.png",
                    response.getOutTradeNo());
            filePathDeepth = filePath.split("/").length;
            logger.info("filePath:" + filePath.split("/")[filePathDeepth - 1]);
        }
        if (response != null && StringUtils.isNotEmpty(response.getQrCode())) {
            ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
        }
        return filePath.split("/")[filePathDeepth - 1];

    }
}


