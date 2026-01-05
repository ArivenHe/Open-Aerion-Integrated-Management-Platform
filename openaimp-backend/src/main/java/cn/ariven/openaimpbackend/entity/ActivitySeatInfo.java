package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "tb_activity_seat_info")
@Entity
public class ActivitySeatInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private int id;
    @Column(name = "activity_id")
    private int activityId;
    @Column(name = "seat_name")
    private String seatName;
    @Column(name = "seat_type")
    private String seatType;
    @Column(name = "seat_rating")
    private String seatRating;
    @Column(name = "seat_freq")
    private String seatFreq;
    @Column(name = "seat_supervision_sta")
    private String seatSupervisionSta;
    @Column(name = "seat_supervision_user_cid")
    private String seatSupervisionUserCid;
    @Column(name = "seat_user_cid")
    private Integer userCid;
    @Column(name = "seat_user_callsign")
    private String userCallsign;
    @Column(name = "seat_user_notam")
    private String seatUserNotam;
}
