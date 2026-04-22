package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.Platform;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformMapper extends JpaRepository<Platform, Integer> {
  Optional<Platform> findFirstByOrderByIdAsc();
}
