package com.needed.task.model;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable =  false)
    private String name;

    @ManyToMany
    private Set <Permission> permissions;

    @OneToMany(mappedBy = "role")
    private Set <User> users;

    @Override
    public String getAuthority()
    {
        return name.toUpperCase();
    }
}