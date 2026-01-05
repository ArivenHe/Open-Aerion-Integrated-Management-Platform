package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.MapConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapConfigRepository extends CrudRepository<MapConfig, Integer> {
}
