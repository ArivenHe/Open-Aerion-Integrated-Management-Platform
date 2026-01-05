package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.BotQQ;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotQQRepository extends CrudRepository<BotQQ, String> {
}
