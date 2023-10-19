package com.cwjy.bs.server.impl;

import com.cwjy.bs.common.MapperTools;
import com.cwjy.bs.common.ResponseEntity;
import com.cwjy.bs.orm.dto.*;
import com.cwjy.bs.orm.entity.*;
import com.cwjy.bs.server.CategoryDetailsServer;
import com.cwjy.bs.server.CommodityServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xgp
 * @version 1.0
 * @date 3/23 10:09
 * @table
 */
@Service
public class CommodityServerImpl extends MapperTools implements CommodityServer {


    @Override
    public ResponseEntity deleteByPrimaryKey(String id) {
        return ResponseEntity.success(commodityMapper.deleteByPrimaryKey(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity insert(Commodity record) {
        record.setSales_volume(0);
        record.setEvaluation(0);
        record.getClassificationDetailedList().stream().forEach(item -> {
            addClassification(record, item);
        });
        return ResponseEntity.success(commodityMapper.insert(record));
    }

    @Override
    public ResponseEntity insertSelective(Commodity record) {
        return null;
    }

    @Override
    public ResponseEntity selectByPrimaryKey(String id) {
        CommodityEntity commodity = commodityMapper.selectByPrimaryKey(id);
        List<CommodityCategoryEntity> categories =
                commodityCategoryMapper.selectByCategoryDetailsId(commodity.getCategory_details());
        categories.stream().forEach(item -> {
            List<ClassificationDetailedEntity> classificationDetailed = classificationDetailedMapper.selectByCommodityCategoryIdAndCommodityId(item.getId(), commodity.getId());
            item.setClassificationDetaileds(classificationDetailed);
        });
        commodity.setCommodityCategoryList(categories);
        return ResponseEntity.success(commodity);
    }

    @Override
    public ResponseEntity updateByPrimaryKeySelective(Commodity record) {
        record.getClassificationDetailedList().stream().forEach(item -> {
            classificationDetailedMapper.deleteByCommodityCategoryIdAndCommodityId(item.getCommodity_category_id(),record.getId());
            addClassification(record, item);
        });
        return ResponseEntity.success(commodityMapper.updateByPrimaryKeySelective(record));
    }

    private void addClassification(Commodity record, ClassificationDetailed item) {
        for (String type_detailed_array : item.getType_detailed_array()) {
            ClassificationDetailed classificationDetailed = new ClassificationDetailed();
            classificationDetailed.initSave(record.getUpdate_user());
            classificationDetailed.setCommodity_category_id(item.getCommodity_category_id());
            classificationDetailed.setCommodity_id(record.getId());
            classificationDetailed.setType_detailed(type_detailed_array);
            classificationDetailed.setStatus(1);
            classificationDetailedMapper.insert(classificationDetailed);
        }
    }

    @Override
    public ResponseEntity updateByPrimaryKey(Commodity record) {
        return null;
    }

    @Override
    public ResponseEntity getPage(Commodity record) {
        List<CommodityEntity> commodityEntities = commodityMapper.getPage(record);
        commodityEntities.stream().forEach(item -> {
            CategoryDetailsEntity detailsEntity = categoryDetailsMapper.selectByPrimaryKey(item.getCategory_details());
            item.setCategory_details_name(detailsEntity.getClassification_name());
            List<CommodityCategoryEntity> commodityCategoryEntities = commodityCategoryMapper.selectByCategoryDetailsId(item.getCategory_details());
            item.setCommodityCategoryList(commodityCategoryEntities);
            commodityCategoryEntities.stream().forEach(itCate ->{
                List<ClassificationDetailedEntity> classificationDetailedEntities = classificationDetailedMapper.selectByCommodityCategoryIdAndCommodityId(itCate.getId(), item.getId());
                itCate.setClassificationDetaileds(classificationDetailedEntities);
            });
        });
        return ResponseEntity.success(commodityEntities);
    }

    @Override
    public ResponseEntity getProductReviews(Commodity record) {
        List<CommodityEntity> commodityEntities = commodityMapper.getPage(record);
        commodityEntities.stream().forEach(item -> {
            ProductReviewsCommodity productReviews = new ProductReviewsCommodity();
            productReviews.setCommodity_id(item.getId());
            productReviews.setParent_node("0");
            List<ProductReviewsCommodityEntity> mapperPage = productReviewsCommodityMapper.getPage(productReviews);
            mapperPage.stream().forEach(SubclassItem -> {
                User user = userMapper.selectByNameUser(SubclassItem.getCommentator());
                SubclassItem.setUserImages(user.getImages_path());
                ProductReviewsCommodity productReviewsSubclass = new ProductReviewsCommodity();
                productReviewsSubclass.setCommodity_id(item.getId());
                productReviewsSubclass.setParent_node(SubclassItem.getId());
                List<ProductReviewsCommodityEntity> reviewsMapperPage = productReviewsCommodityMapper.getPage(productReviewsSubclass);

                reviewsMapperPage.stream().forEach(itemPage -> {
                    String images_path = userMapper.selectByNameUser(itemPage.getCommentator()) == null ? null :
                            userMapper.selectByNameUser(itemPage.getCommentator()).getImages_path();
                    itemPage.setUserImages(images_path);
                });
                SubclassItem.setProductReviewsList(reviewsMapperPage);
            });
            item.setProductReviewsList(mapperPage);
        });
        return ResponseEntity.success(commodityEntities);
    }
}