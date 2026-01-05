package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "tb_activity")
@Entity
public class ActivityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "activity_title")
    private String title;
    @Column(name = "activity_time")
    private String time;
    @Column(name = "activity_image")
    private String image;
    @Column(name = "activity_type")
    private int type;
    @Column(name = "activity_status")
    private int status;
    @Column(name = "activity_dep")
    private String dep;
    @Column(name = "activity_arr")
    private String arr;
    @Column(name = "activity_direction")
    private String direction;
    @Column(name = "activity_direction_opposite")
    private String direction_opposite;
    @Column(name = "activity_direction_route")
    private String route;
    @Column(name = "activity_direction_route_opposite")
    private String route_opposite;
    @Column(name = "activity_notams")
    private String notams;
}
