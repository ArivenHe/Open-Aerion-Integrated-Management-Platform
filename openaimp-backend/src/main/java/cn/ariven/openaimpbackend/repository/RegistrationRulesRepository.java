package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.RegistrationRules;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RegistrationRulesRepository extends CrudRepository<RegistrationRules, String> {
    boolean existsById(String callsign);
    Optional<RegistrationRules> findById(String callsign);
}
