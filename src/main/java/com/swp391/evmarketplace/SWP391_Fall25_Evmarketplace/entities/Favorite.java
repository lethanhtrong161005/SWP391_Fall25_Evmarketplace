package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite",
        uniqueConstraints = @UniqueConstraint(name = "uq_fav_acc_listing", columnNames = {"account_id","listing_id"}),
        indexes = {
                @Index(name = "idx_fav_acc", columnList = "account_id"),
                @Index(name = "idx_fav_listing", columnList = "listing_id")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne( optional = false)
    @JoinColumn(name = "account_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_fav_acc"))
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(name = "listing_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_fav_listing"))
    private Listing listing;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
