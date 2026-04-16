package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.Atc;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtcMapper extends JpaRepository<Atc, Long> {
  Optional<Atc> findByUserId(Integer userId);

  boolean existsByUserId(Integer userId);

  List<Atc> findAllByOrderByUserIdAsc();

  void deleteByUserId(Integer userId);
}
