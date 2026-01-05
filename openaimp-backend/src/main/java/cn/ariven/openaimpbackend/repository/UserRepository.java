package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    boolean existsByEmail(String email);
    boolean existsByCallsign(String callsign);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.rating = :newRating WHERE u.cid = :cid")
    int updateRatingByCid(int cid, int newRating);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.qq = :newQQ WHERE u.cid = :cid")
    int setNewQQ(String cid, String newQQ);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.studentId = :studentId WHERE u.cid = :cid")
    int setNewStudentId(String cid, String studentId);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findUserByEmail(String email);

    @Query("SELECT u.cid From User u WHERE u.email=:email")
    int findUserIdByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.callsign = :callsign")
    User findUserByCallsign(String callsign);
}
