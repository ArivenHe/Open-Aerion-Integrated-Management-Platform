package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.pojo.ActivityDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDetailRepository extends JpaRepository<ActivityDetail, Long> {
}
