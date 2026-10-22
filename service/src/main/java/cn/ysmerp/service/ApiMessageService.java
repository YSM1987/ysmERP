package cn.ysmerp.service;

import cn.ysmerp.common.ResultVo;
import cn.ysmerp.common.mq.MqMessage;

public interface ApiMessageService {
    //    ResultVo<Integer> jdOrderMessage(String orderId);
//
//    ResultVo<Integer> jdvcOrderMessage(String orderId );
//    ResultVo<Integer> taoOrderMessage(String tid  );
//    ResultVo<Integer> pddOrderMessage(String orderSn  );
//    ResultVo<Integer> douOrderMessage(String orderId );
//    ResultVo<Integer> offlineOrderMessage(String orderNum);
//    ResultVo<Integer> weiOrderMessage(String orderNum );
    ResultVo<Integer> messageHandle(MqMessage mqMessage);
}
