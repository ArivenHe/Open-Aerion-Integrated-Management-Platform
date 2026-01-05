package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "tb_activity_pilot")
@Entity
public class ActivityPilot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name="user_cid")
    private int user_cid;
    @Column(name="activity_id")
    private int activity_id;
    @Column(name = "user_callsign")
    private String user_callsign;
    @Column(name="clearance_method")
    private String clearance_method;
}
