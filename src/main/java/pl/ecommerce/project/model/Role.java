package pl.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.Data;
import pl.ecommerce.project.model.app.AppRole;

@Data
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name")
    private AppRole role;

}
