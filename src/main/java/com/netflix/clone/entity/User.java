package com.netflix.clone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.netflix.clone.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean active=true;

    @Column(nullable = false)
    private boolean emailVerified=false;

    @Column(unique = true)
    private String verificationToken;

    @Column
    private Instant verificationTokenExpiry;

    @Column
    private String passwordResetToken;

    @Column
    private Instant passwordResetTokenExpiry;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_watchlist",
            joinColumns = @JoinColumn(name = "user_email"),
            inverseJoinColumns = @JoinColumn(name = "vedio_id")
    )
    private Set<Vedio> watchlist=new HashSet<>();

    public void addToWatchList(Vedio vedio){
        this.watchlist.add(vedio);
    }

    public void removeFromWatchList( Vedio vedio){
        this.watchlist.remove(vedio);
    }

}
