package cn.ariven.openaimpbackend.pojo;

import cn.ariven.openaimpbackend.enumeration.AirportRating;
import cn.ariven.openaimpbackend.enumeration.FlightDirection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "activity_detail")
public class ActivityDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activityTime;

    private String depIcao;

    private String appIcao;

    private AirportRating airportRating;

    private String notams;

    private FlightDirection flightDirection;


}
