package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.pojo.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
