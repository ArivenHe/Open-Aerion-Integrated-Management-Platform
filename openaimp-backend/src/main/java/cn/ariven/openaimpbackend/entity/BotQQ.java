package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "tb_qq_bot")
@Entity
public class BotQQ {
    @Id
    private String appId;
    private String appSecret;
}
