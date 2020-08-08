package org.seckill.service.common.trade.alipay;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.seckill.mp.dao.mapper.GoodsMapper;
import org.seckill.service.common.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 支付宝扫一扫SDK，根据官方示例改造，基于nacos配置中心注入配置
 * 官方文档
 *      https://opendocs.alipay.com/open/00y8k9
 *
 * @author heng
 */
@Component
@Slf4j
public class AlipayRunner {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RedisService redisService;

    @Value("${alipay.merchantPrivateKey:1}")
    private String merchantPrivateKey;

    @Value("${alipay.alipayPublicKey:1}")
    private String alipayPublicKey;

    @Value("${alipay.notifyUrl:1}")
    private String notifyUrl;

    @Value("${alipay.encryptKey:1}")
    private String encryptKey;

    @Value("${alipay.appId:1}")
    private String appId;

    @Value("${alipay.gatewayHost:openapi.alipaydev.com}")
    private String gatewayHost;

    @Value("${alipay.signType:RSA2}")
    private String signType;

    @Value("${alipay.qrcodeImagePath:1}")
    private String qrcodeImagePath;

    private Config getOptions() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = gatewayHost;
        config.signType = signType;

        config.appId = appId;
        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = merchantPrivateKey;

        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
//        config.merchantCertPath = "<-- 请填写您的应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt -->";
//        config.alipayCertPath = "<-- 请填写您的支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt -->";
//        config.alipayRootCertPath = "<-- 请填写您的支付宝根证书文件路径，例如：/foo/alipayRootCert.crt -->";

        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        config.alipayPublicKey = alipayPublicKey;

        //可设置异步通知接收服务地址（可选）
        config.notifyUrl = notifyUrl;

        //可设置AES密钥，调用AES加解密相关接口时需要（可选）
        config.encryptKey = encryptKey;

        return config;
    }


    public String tradePrecreate(long seckillId) {
        Seckill seckill = redisService.getSeckill(seckillId);
        Goods goods = goodsMapper.selectById(seckill.getGoodsId());
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);
        // 1. 设置参数（全局只需设置一次）
        Factory.setOptions(getOptions());
        try {
            // 2. 发起API调用（以创建当面付收款二维码为例）
            AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
                    .preCreate(goods.getName(), outTradeNo, String.valueOf(seckill.getPrice()));
            // 3. 处理响应或异常
            if (ResponseChecker.success(response)) {
                String finalQrcodeImagePath = String.format(qrcodeImagePath + "/qr-%s.png",
                        response.outTradeNo);
                ZxingUtils.getQRCodeImge(response.qrCode, 256, finalQrcodeImagePath);
                log.info("支付宝当面扫调用成功！");
                return finalQrcodeImagePath.split("/")[finalQrcodeImagePath.split("/").length - 1];
            } else {
                log.error("调用失败，原因：" + response.msg + "，" + response.subMsg);
            }
        } catch (Exception e) {
            log.error("调用遭遇异常，原因：" + e.getMessage());
        }
        return null;
    }


}
