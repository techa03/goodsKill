package com.goodskill.mongo.repository;

import com.goodskill.mongo.entity.SuccessKilledDto;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

/**
 * @author techa03
 * @date 2019/4/3
 */
public interface SuccessKilledRepository extends CrudRepository<SuccessKilledDto, BigInteger> {
}
