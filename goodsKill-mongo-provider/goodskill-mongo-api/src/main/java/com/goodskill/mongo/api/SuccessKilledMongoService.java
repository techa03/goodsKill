package com.goodskill.mongo.api;

import com.goodskill.mongo.entity.SuccessKilledDto;

/**
 * @author techa03
 * @date 2019/4/6
 */
public interface SuccessKilledMongoService {

    /**
     * @param sekcillId
     * @return
     */
    long deleteRecord(long sekcillId);

    /**
     * @param successKilledDto
     * @return
     */
    void saveRecord(SuccessKilledDto successKilledDto);

    /**
     * @param successKilledDto
     * @return
     */
    long count(SuccessKilledDto successKilledDto);
}
