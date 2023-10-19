package com.cwjy.bs.server.impl;

import com.cwjy.bs.common.EnumCommon;
import com.cwjy.bs.common.MapperTools;
import com.cwjy.bs.common.ResponseEntity;
import com.cwjy.bs.config.BaseException;
import com.cwjy.bs.orm.dto.Order;
import com.cwjy.bs.orm.dto.OrderReturn;
import com.cwjy.bs.orm.entity.OrderReturnEntity;
import com.cwjy.bs.server.OrderReturnServer;
import com.cwjy.bs.server.OrderReturnServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author xgp
 * @version 1.0
 * @date 3/23 10:09
 * @table
 */
@Service
public class OrderReturnServerImpl extends MapperTools implements OrderReturnServer {


    @Override
    public ResponseEntity deleteByPrimaryKey(String id) {
        return ResponseEntity.success(orderReturnMapper.deleteByPrimaryKey(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity insert(OrderReturn record) {
        OrderReturnEntity orderReturnEntity = orderReturnMapper.selectByOrderId(record.getOrder_id());
        if(orderReturnEntity != null){
            throw new BaseException("当前退款已存在，请勿重新申请！");
        }
        Order order = new Order();
        order.setId(record.getOrder_id());
        order.setStatus(EnumCommon.OrderStatus.REFUNDING.getCode());
        orderMapper.updateByPrimaryKeySelective(order);
        record.setStatus(1);
        return ResponseEntity.success(orderReturnMapper.insert(record));
    }


    @Override
    public ResponseEntity insertSelective(OrderReturn record) {
        return ResponseEntity.success(orderReturnMapper.insertSelective(record));
    }

    @Override
    public ResponseEntity selectByPrimaryKey(String id) {
        return ResponseEntity.success(orderReturnMapper.selectByPrimaryKey(id));
    }

    @Override
    public ResponseEntity updateByPrimaryKeySelective(OrderReturn record) {
        return ResponseEntity.success(orderReturnMapper.updateByPrimaryKeySelective(record));
    }

    @Override
    public ResponseEntity updateByPrimaryKey(OrderReturn record) {
        return ResponseEntity.success(orderReturnMapper.updateByPrimaryKey(record));
    }

    @Override
    public ResponseEntity getPage(OrderReturn record) {
        return ResponseEntity.success(null);
    }

    @Override
    public ResponseEntity agreeToRefund(OrderReturn record) {
        OrderReturnEntity entity = orderReturnMapper.selectByPrimaryKey(record.getId());
        if(entity.getRefund_type() == EnumCommon.OrderRefundType.ONLY_REFUND.getCode()){
            record.setStatus(EnumCommon.OrderReturnStatus.COMPLETED.getCode());
            Order order = new Order();
            order.initUpdate(record.getUpdate_user());
            order.setStatus(EnumCommon.OrderStatus.TRANSACTION_FAILED.getCode());
            order.setId(entity.getOrder_id());
            order.setClose_time(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            record.setStatus(EnumCommon.OrderReturnStatus.WAITING_FOR_INPUT.getCode());
        }
        return ResponseEntity.success(orderReturnMapper.updateByPrimaryKeySelective(record));
    }

    @Override
    public ResponseEntity refusalToRefund(OrderReturn record) {
        record.setStatus(EnumCommon.OrderReturnStatus.REJECTED.getCode());
        return ResponseEntity.success(orderReturnMapper.updateByPrimaryKeySelective(record));
    }

    @Override
    public ResponseEntity requestRefundAgain(OrderReturn record) {
        OrderReturnEntity returnEntity = orderReturnMapper.selectByOrderId(record.getOrder_id());
        if(returnEntity.getStatus() == EnumCommon.OrderReturnStatus.REJECTED.getCode() ){
            record.setStatus(EnumCommon.OrderReturnStatus.APPLYING.getCode());
            record.setId(returnEntity.getId());
            int update = orderReturnMapper.updateByPrimaryKeySelective(record);
            return ResponseEntity.success(update);
        }
        return ResponseEntity.error("修改失败");
    }
}