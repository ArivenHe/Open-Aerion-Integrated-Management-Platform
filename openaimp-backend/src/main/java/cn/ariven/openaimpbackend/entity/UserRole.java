package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name="tb_user_role")
@Entity
public class UserRole {
    @Id
    private String value; // Role code, e.g. "admin"
    private String label; // Role name, e.g. "Administrator"

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tb_role_permission",
        joinColumns = @JoinColumn(name = "role_value"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
}
