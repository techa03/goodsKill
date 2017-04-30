package org.seckill.common.trade.alipay;

import com.alipay.demo.trade.model.builder.AlipayHeartbeatSynRequestBuilder;
import com.alipay.demo.trade.model.hb.*;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.impl.hb.AbsHbRunner;
import com.alipay.demo.trade.service.impl.hb.HbQueue;
import com.alipay.demo.trade.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuyangkly on 15/10/23.
 执行调度，主要任务由两个线程完成，交易线程（调用当面付2.0接口）和交易保障线程（轮询），具体需要做的事情
 1.当面付程序每执行完一笔交易后将交易结果保存在临时队列
 2.轮询线程读取临时队列，获取基础采集信息和最多30条trade_info信息，调用支付宝monitor.heartbeat.syn接口
 示例代码仅封装了如何调用该接口api，采集数据，比如采集网络信息、交易耗时、异常信息等，需要系统商开发者自行完成。
 */
public class DemoHbRunner extends AbsHbRunner {

    public DemoHbRunner(AlipayMonitorService monitorService) {
        super(monitorService);
    }

    @Override
    public String getAppAuthToken() {
        // 对于系统商，如果是为了商户开发监控保障接口，则需要传此值，否则如果为系统商自己做交易保障接口开发，则可不传。
        return null;
    }

    @Override
    public AlipayHeartbeatSynRequestBuilder getBuilder() {
        // 系统商使用的交易信息格式，json字符串类型，从交易队列中获取
        List<SysTradeInfo> sysTradeInfoList = HbQueue.poll();

        // 异常信息的采集，系统商自行完成
        List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
        //        exceptionInfoList.add(ExceptionInfo.HE_SCANER);
        //        exceptionInfoList.add(ExceptionInfo.HE_PRINTER);
        //        exceptionInfoList.add(ExceptionInfo.HE_OTHER);

        AlipayHeartbeatSynRequestBuilder builder = new AlipayHeartbeatSynRequestBuilder()
            .setProduct(Product.FP).setType(Type.CR).setEquipmentId("cr1000001")
            .setEquipmentStatus(EquipStatus.NORMAL).setTime(Utils.toDate(new Date()))
            .setStoreId("store10001").setMac("0a:00:27:00:00:00").setNetworkType("LAN")
            .setProviderId("2088911212323549") // 设置系统商pid
            .setSysTradeInfoList(sysTradeInfoList) // 系统商同步trade_info信息
            .setExceptionInfoList(exceptionInfoList) // 填写异常信息，如果有的话
        ;
        return builder;
    }
}
