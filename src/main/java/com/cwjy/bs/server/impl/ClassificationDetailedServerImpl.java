package com.cwjy.bs.server.impl;

import com.cwjy.bs.common.ResponseEntity;
import com.cwjy.bs.orm.dto.ClassificationDetailed;
import com.cwjy.bs.server.CategoryDetailsServer;
import com.cwjy.bs.server.ClassificationDetailedServer;
import org.springframework.stereotype.Service;

/**
 * @author xgp
 * @version 1.0
 * @date 3/23 10:09
 * @table
 */
@Service
public class ClassificationDetailedServerImpl implements ClassificationDetailedServer {


    @Override
    public ResponseEntity deleteByPrimaryKey(String id) {
        return null;
    }

    @Override
    public ResponseEntity insert(ClassificationDetailed record) {
        return null;
    }

    @Override
    public ResponseEntity insertSelective(ClassificationDetailed record) {
        return null;
    }

    @Override
    public ResponseEntity selectByPrimaryKey(String id) {
        return null;
    }

    @Override
    public ResponseEntity updateByPrimaryKeySelective(ClassificationDetailed record) {
        return null;
    }

    @Override
    public ResponseEntity updateByPrimaryKey(ClassificationDetailed record) {
        return null;
    }
}