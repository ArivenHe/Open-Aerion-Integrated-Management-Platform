package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.CorsWhitelist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorsWhitelistRepository extends CrudRepository<CorsWhitelist, Integer> {
}
