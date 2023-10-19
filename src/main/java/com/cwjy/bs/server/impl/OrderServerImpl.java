package com.cwjy.bs.server.impl;

import com.cwjy.bs.common.EnumCommon;
import com.cwjy.bs.common.MapperTools;
import com.cwjy.bs.common.ResponseEntity;
import com.cwjy.bs.orm.dto.*;
import com.cwjy.bs.orm.entity.*;
import com.cwjy.bs.server.CommodityServer;
import com.cwjy.bs.server.OrderServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author xgp
 * @version 1.0
 * @date 3/23 10:09
 * @table
 */
@Service
public class OrderServerImpl extends MapperTools implements OrderServer {


    @Override
    public ResponseEntity deleteByPrimaryKey(String id) {
        return ResponseEntity.success(orderMapper.deleteByPrimaryKey(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity insert(Order record) {
        record.setStatus(EnumCommon.OrderStatus.ALREADY_PAID.getCode());
        record.setPayment_time(new Date());
        record.setPayment_type(EnumCommon.OrderPaymentTypes.OTHER.getCode());
        record.setUser_id(record.getCreate_user());
        record.setBuyer_nick("冯总小念子");
        record.setBuyer_rate(EnumCommon.OrderBuyerRate.NO.getCode());
        int insert = orderMapper.insert(record);
        /**增加订单商品详细*/
        addOrderItem(record);
        /**增加收货信息*/
        addShippingItem(record);

        return ResponseEntity.success(insert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity insertList(List<Order> record) {
        record.stream().forEach(item -> {
            item.setStatus(EnumCommon.OrderStatus.ALREADY_PAID.getCode());
            item.setPayment_time(new Date());
            item.setPayment_type(EnumCommon.OrderPaymentTypes.OTHER.getCode());
            item.setUser_id(item.getCreate_user());
            item.setBuyer_nick("冯总小念子");
            item.setBuyer_rate(EnumCommon.OrderBuyerRate.NO.getCode());
            /**增加订单商品详细*/
            addOrderItem(item);
            /**增加收货信息*/
            addShippingItem(item);
            /**删除购物车物品信息*/
            Arrays.stream(item.getShopping_cart_id()).forEach(itemCart -> {
                shoppingCartMapper.deleteByPrimaryKey(itemCart);
            });
            /**商品销售数量增加*/
        });
        return ResponseEntity.success(orderMapper.insertList(record));
    }

    private void addShippingItem(Order record) {
        OrderShippingItem orderShippingItem = new OrderShippingItem();
        orderShippingItem.setId(UUID.randomUUID().toString().replaceAll("-",""));
        orderShippingItem.setOrder_id(record.getId());
        orderShippingItem.setOrder_shipping_id(record.getOrder_shipping_id());
        orderShippingItemMapper.insert(orderShippingItem);
    }

    private void addOrderItem(Order record) {
        record.getOrderItem().stream().forEach(item -> {
            item.setId(UUID.randomUUID().toString().replaceAll("-",""));
            item.setOrder_id(record.getId());
            orderItemMapper.insert(item);
            /**商品销售数量增加*/
            commodityMapper.addSalesVolume(item.getItem_id());
        });
    }

    @Override
    public ResponseEntity insertSelective(Order record) {
        return ResponseEntity.success(orderMapper.insertSelective(record));
    }

    @Override
    public ResponseEntity selectByPrimaryKey(String id) {
        return ResponseEntity.success(orderMapper.selectByPrimaryKey(id));
    }

    @Override
    public ResponseEntity updateByPrimaryKeySelective(Order record) {
        return ResponseEntity.success(orderMapper.updateByPrimaryKeySelective(record));
    }

    @Override
    public ResponseEntity updateByPrimaryKey(Order record) {
        return ResponseEntity.success(orderMapper.updateByPrimaryKey(record));
    }

    @Override
    public ResponseEntity getPage(Order record) {
        List<OrderEntity> orderEntities = orderMapper.getPage(record);
        orderEntities.stream().forEach(item -> {
            OrderShippingItem orderShippingItem = orderShippingItemMapper.getByOrderId(item.getId());
           if(orderShippingItem != null){
               OrderShippingEntity orderShippingEntity = orderShippingMapper.selectByPrimaryKey(orderShippingItem.getOrder_shipping_id());
               item.setOrderShippingEntity(orderShippingEntity);
               List<OrderItemEntity> orderItemEntity = orderItemMapper.getByOrderId(item.getId());
               orderItemEntity.stream().forEach(orderItem -> {
                   CommodityEntity commodityEntity = commodityMapper.selectByPrimaryKey(orderItem.getItem_id());
                   orderItem.setCommodityEntity(commodityEntity);
               });
               item.setOrderItemEntity(orderItemEntity);
           }
           /**判断是否有退货信息*/
           if(item.getStatus() == EnumCommon.OrderStatus.REFUNDING.getCode()  || item.getStatus() == EnumCommon.OrderStatus.TRANSACTION_FAILED.getCode()){
               OrderReturnEntity orderReturnEntity = orderReturnMapper.selectByOrderId(item.getId());
               orderReturnEntity.setStatusName(EnumCommon.OrderReturnStatus.getNameByCode(orderReturnEntity.getStatus()));
                item.setOrderReturnEntity(orderReturnEntity);
           }
           /**注入状态描述*/
            item.setStatusName(EnumCommon.OrderStatus.getNameByCode(item.getStatus()));
        });

        return ResponseEntity.success(orderEntities);
    }

    @Override
    public ResponseEntity confirmReceipt(Order record) {
        record.setStatus(EnumCommon.OrderStatus.SUCCESSFUL_TRANSACTION.getCode());
        return ResponseEntity.success( orderMapper.updateByPrimaryKeySelective(record));
    }
}