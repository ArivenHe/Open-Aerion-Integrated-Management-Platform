package cn.ariven.openaimpbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(name = "tb_user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cid")
    private int cid;

    @Column(name = "user_callsign")
    private String callsign;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_rating", nullable = false)
    @ColumnDefault("1")
    private int rating = 1;

    @Column(name = "user_created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "user_qq")
    private String qq;

    @Column(name = "user_group")
    private String group; // Keep for backward compatibility or simple comma-separated storage if needed

    @Column(name="tb_atc_rating")
    @ColumnDefault("1")
    private Integer atcRating;

    @Column(name = "tb_user_student_id")
    private String studentId;

    // RBAC relation
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tb_user_roles_mapping",
        joinColumns = @JoinColumn(name = "user_cid"),
        inverseJoinColumns = @JoinColumn(name = "role_value")
    )
    private List<UserRole> roles;
}
