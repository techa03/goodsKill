package com.goodskill.mongo.service;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilledDto;
import com.goodskill.mongo.repository.SuccessKilledRepository;
import com.mongodb.client.result.DeleteResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author heng
 */
@Service(version = "1.0.0")
public class SuceessKillMongoService implements SuccessKilledMongoService {
    @Autowired
    private SuccessKilledRepository successKilledRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long deleteRecord(long sekcillId) {
        DeleteResult deleteResult = mongoTemplate.remove(query(where("seckillId").is(String.valueOf(sekcillId))), SuccessKilledDto.class);
        return deleteResult.getDeletedCount();
    }

    @Override
    public long count(SuccessKilledDto successKilledDto) {
        return mongoTemplate.count(query(where("seckillId").is(successKilledDto.getSeckillId())), SuccessKilledDto.class);
    }
}