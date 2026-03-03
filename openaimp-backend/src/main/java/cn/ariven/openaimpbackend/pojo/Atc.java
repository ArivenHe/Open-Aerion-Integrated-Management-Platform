package cn.ariven.openaimpbackend.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "atc_list")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Atc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private Integer radarRating;
    private String atcGroup;
}
