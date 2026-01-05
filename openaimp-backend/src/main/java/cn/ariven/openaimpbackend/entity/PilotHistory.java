package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "tb_history_pilot")
@Entity
public class PilotHistory {
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
    @Column(name = "dep")
    private String dep;
    @Column(name = "arr")
    private String arr;
    @Column(name = "aircraft")
    private String aircraft;
    @Column(name = "conndate")
    private String conndate;

    public long getOnlineTime() {
        return onlinetime;
    }
}
