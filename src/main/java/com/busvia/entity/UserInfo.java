package com.busvia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {

    @Id
    private UUID uuid;
    private  String firstName;
    private  String email;
    private  String password;
    private  String role;
    private boolean isBlock;
}
