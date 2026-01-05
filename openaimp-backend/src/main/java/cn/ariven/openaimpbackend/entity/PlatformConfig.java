package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_platform_config")
public class PlatformConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int firstInstall = 0;
    private int mapModel = 0;
}
