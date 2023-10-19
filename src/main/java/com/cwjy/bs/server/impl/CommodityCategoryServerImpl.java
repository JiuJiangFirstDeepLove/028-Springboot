package com.cwjy.bs.server.impl;

import com.cwjy.bs.common.ResponseEntity;
import com.cwjy.bs.orm.dto.CommodityCategory;
import com.cwjy.bs.server.CategoryDetailsServer;
import com.cwjy.bs.server.CommodityCategoryServer;
import org.springframework.stereotype.Service;

/**
 * @author xgp
 * @version 1.0
 * @date 3/23 10:09
 * @table
 */
@Service
public class CommodityCategoryServerImpl implements CommodityCategoryServer {


    @Override
    public ResponseEntity deleteByPrimaryKey(String id) {
        return null;
    }

    @Override
    public ResponseEntity insert(CommodityCategory record) {
        return null;
    }

    @Override
    public ResponseEntity insertSelective(CommodityCategory record) {
        return null;
    }

    @Override
    public ResponseEntity selectByPrimaryKey(String id) {
        return null;
    }

    @Override
    public ResponseEntity updateByPrimaryKeySelective(CommodityCategory record) {
        return null;
    }

    @Override
    public ResponseEntity updateByPrimaryKey(CommodityCategory record) {
        return null;
    }
}