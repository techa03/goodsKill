package com.goodskill.mongoreactive;

import com.goodskill.mongoreactive.entity.SuccessKilled;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

/**
 * @author techa03
 * @date 2019/4/3
 */
public interface SuccessKilledRepository extends CrudRepository<SuccessKilled, BigInteger> {
}
