package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "tb_registration_rules")
@Entity
public class RegistrationRules {
    @Id
    private String callsign;
}
