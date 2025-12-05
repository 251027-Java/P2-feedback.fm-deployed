package com.revature.ExpenseReport.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Entity
@Table(
        name = "app_users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"username"})
        }
)
@Data
@NoArgsConstructor
public class AppUser {
    // Fields
    @Id
    @GeneratedValue
    private Long userid;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_role")
    private String userrole;

    // Constructor
    public AppUser( String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.userrole = role;
    }

    // Methods
}
