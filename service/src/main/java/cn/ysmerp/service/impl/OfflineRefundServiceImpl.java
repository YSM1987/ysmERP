package cn.ysmerp.service.impl;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.OfflineRefund;
import cn.ysmerp.mapper.OfflineRefundMapper;
import cn.ysmerp.service.OfflineRefundService;
import cn.ysmerp.request.RefundSearchRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【offline_refund(线下渠道退款表)】的数据库操作Service实现
* @createDate 2024-09-16 13:31:26
*/
@AllArgsConstructor
@Service
public class OfflineRefundServiceImpl extends ServiceImpl<OfflineRefundMapper, OfflineRefund>
    implements OfflineRefundService {
    private final OfflineRefundMapper refundMapper;

    @Override
    public PageResult<OfflineRefund> queryPageList(RefundSearchRequest bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OfflineRefund> queryWrapper = new LambdaQueryWrapper<OfflineRefund>();

        Page<OfflineRefund> pages = refundMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




