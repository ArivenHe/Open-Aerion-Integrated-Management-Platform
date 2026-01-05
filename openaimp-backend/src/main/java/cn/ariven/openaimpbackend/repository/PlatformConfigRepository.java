package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.PlatformConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformConfigRepository extends CrudRepository<PlatformConfig, Integer> {
}
