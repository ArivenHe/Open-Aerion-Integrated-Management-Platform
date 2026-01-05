package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.AtcHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnlineAtcRepository extends CrudRepository<AtcHistory, Integer> {
    @Query("SELECT a FROM AtcHistory a WHERE a.cid = :cid")
    List<AtcHistory> findAtcHistoriesByCid(int cid);
}
