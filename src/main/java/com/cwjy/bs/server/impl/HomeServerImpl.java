package com.cwjy.bs.server.impl;

import com.cwjy.bs.common.EnumCommon;
import com.cwjy.bs.common.MapperTools;
import com.cwjy.bs.common.ResponseEntity;
import com.cwjy.bs.orm.entity.HomeEntity;
import com.cwjy.bs.server.HomeServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xgp
 * @version 1.0
 * @date 4/10 14:00
 * @table
 * @description
 */
@Service
public class HomeServerImpl extends MapperTools implements HomeServer {
    @Override
    public ResponseEntity dailyOrder() {
        return ResponseEntity.success(orderMapper.dailyOrder());
    }

    @Override
    public ResponseEntity fosterCareClassification() {
        List<HomeEntity> entities = petBoardingMapper.fosterCareClassification();
        entities.stream().forEach(item -> item.setName(EnumCommon.PetType.getNameByCode(Integer.parseInt(item.getData()))));
        return ResponseEntity.success(entities);
    }

    @Override
    public ResponseEntity salesRanking() {
        return ResponseEntity.success(commodityMapper.salesRanking());
    }

    @Override
    public ResponseEntity dailyFosterOrder() {
        return ResponseEntity.success(petBoardingMapper.dailyFosterOrder());
    }
}
