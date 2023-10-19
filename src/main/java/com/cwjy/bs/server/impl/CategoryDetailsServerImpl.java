package com.cwjy.bs.server.impl;

import com.cwjy.bs.common.MapperTools;
import com.cwjy.bs.common.ResponseEntity;
import com.cwjy.bs.orm.dto.CategoryDetails;
import com.cwjy.bs.orm.dto.CommodityCategory;
import com.cwjy.bs.orm.entity.CategoryDetailsEntity;
import com.cwjy.bs.orm.entity.CommodityCategoryEntity;
import com.cwjy.bs.orm.mapper.CategoryDetailsMapper;
import com.cwjy.bs.orm.mapper.CommodityCategoryMapper;
import com.cwjy.bs.server.CategoryDetailsServer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xgp
 * @version 1.0
 * @date 3/23 10:09
 * @table
 */
@Service
public class CategoryDetailsServerImpl extends MapperTools implements CategoryDetailsServer {


    @Override
    public ResponseEntity deleteByPrimaryKey(String id) {
        commodityCategoryMapper.deleteByCategoryDetailsId(id);
        List<CommodityCategoryEntity> entities = commodityCategoryMapper.selectByCategoryDetailsId(id);
        entities.stream().forEach(item -> {
            classificationDetailedMapper.deleteByCommodityCategoryId(item.getId());
        });
        return ResponseEntity.success(categoryDetailsMapper.deleteByPrimaryKey(id));
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity insert(CategoryDetails record) {
        List<CommodityCategory> list = getCommodityCategories(record);
        commodityCategoryMapper.insert(list);
        return ResponseEntity.success(categoryDetailsMapper.insert(record));
    }

    @Override
    public ResponseEntity insertSelective(CategoryDetails record) {
        return null;
    }

    @Override
    public ResponseEntity selectByPrimaryKey(String id) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity updateByPrimaryKeySelective(CategoryDetails record) {
        List<CommodityCategory> list = getCommodityCategories(record);
        commodityCategoryMapper.deleteByCategoryDetailsId(record.getId());
        commodityCategoryMapper.insert(list);
        return ResponseEntity.success(categoryDetailsMapper.updateByPrimaryKeySelective(record));
    }

    private List<CommodityCategory> getCommodityCategories(CategoryDetails record) {
        List<CommodityCategory> list = new ArrayList<>();
        for (String commodity : record.getCommodityCategory()) {
            CommodityCategory commodityCategory = new CommodityCategory();
            commodityCategory.setType_name(commodity);
            commodityCategory.initSave("admin");
            commodityCategory.setStatus(1);
            commodityCategory.setCategory_details_id(record.getId());
            list.add(commodityCategory);
        }
        return list;
    }

    @Override
    public ResponseEntity updateByPrimaryKey(CategoryDetails record) {
        return null;
    }

    @Override
    public ResponseEntity getPage(CategoryDetails record) {
        List<CategoryDetailsEntity> detailsMapperPage = categoryDetailsMapper.getPage(record);
        detailsMapperPage.stream().forEach(item -> {
            List<CommodityCategoryEntity> categoryEntities = commodityCategoryMapper.selectByCategoryDetailsId(item.getId());
            item.setCommodityCategory(categoryEntities);
        });
        return ResponseEntity.success(detailsMapperPage);
    }
}