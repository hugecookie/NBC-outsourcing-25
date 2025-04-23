package org.example.outsourcing.redis.repository;

import org.example.outsourcing.redis.vo.BlackList;
import org.springframework.data.repository.CrudRepository;

public interface RedisRepository extends CrudRepository<BlackList, String> {
}
