package cn.ysmerp.model.bo;

import lombok.Data;

@Data
public class RefundSearchBo {
    private Integer shopId;
    private String orderNum;
    private Integer refundType;
    private String refundNum;
    private Integer erpPushStatus;
    private Integer hasProcessing;
}
