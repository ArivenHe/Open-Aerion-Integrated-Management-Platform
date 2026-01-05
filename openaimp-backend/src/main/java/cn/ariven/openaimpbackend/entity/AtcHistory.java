package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "tb_history_atc")
@Entity
public class AtcHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private int id;
    @Column(name = "user_cid")
    private int cid;
    @Column(name = "callsign")
    private String callsign;
    @Column(name = "onlinetime")
    private int onlinetime;
    @Column(name = "frequency")
    private String frequency;
    @Column(name = "conndate")
    private String conndate;

    public long getOnlineTime() {
        return onlinetime;
    }
}
