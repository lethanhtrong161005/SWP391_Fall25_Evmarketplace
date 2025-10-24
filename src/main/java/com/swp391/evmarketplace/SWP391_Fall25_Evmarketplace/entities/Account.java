package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AccountRole role = AccountRole.MEMBER;  // MEMBER, ADMIN, STAFF

    @Column(name = "is_phone_verified")
    private boolean phoneVerified = false;

    @Column(name = "is_email_verified")
    private boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;  // ACTIVE, SUSPENDED

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Profile profile;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Listing> listings = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<ListingStatusHistory> listingStatusChanges = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Listing> moderatedListings = new ArrayList<>();

    @OneToMany(mappedBy = "moderationLockedBy", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @BatchSize(size = 50)
    private List<Listing> moderationLockedListings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonIgnore @ToString.Exclude
    private Branch branch;

    @OneToOne(mappedBy = "manager")
    @JsonIgnore
    @ToString.Exclude
    private Branch managedBranch;

    @OneToMany(mappedBy = "responsibleStaff", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Listing> responsibleStaffListings = new ArrayList<>();

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<ConsignmentAgreement> consignmentAgreements = new ArrayList<>();

    @OneToMany(mappedBy="buyer")
    @JsonIgnore
    @ToString.Exclude
    private List<SaleOrder> ordersBought  = new ArrayList<>();

    @OneToMany(mappedBy="seller")
    @JsonIgnore
    @ToString.Exclude
    private List<SaleOrder> ordersSold    = new ArrayList<>();

    @OneToMany(mappedBy="createdBy")
    @JsonIgnore
    @ToString.Exclude
    private List<SaleOrder> ordersCreated = new ArrayList<>();

    @OneToMany(mappedBy = "recordedBy")
    private List<SalePayment> cashPaymentsRecorded =  new ArrayList<>();



    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public AccountReponseDTO toDto(Account account, String serverUrl) {
        AccountReponseDTO dto = new AccountReponseDTO();
        dto.setId(account.getId());
        dto.setPhoneNumber(account.getPhoneNumber());
        dto.setEmail(account.getEmail());
        dto.setRole(account.getRole());
        dto.setStatus(account.getStatus());
        dto.setEmailVerified(account.isEmailVerified());
        dto.setPhoneVerified(account.isPhoneVerified());
        dto.setProfile(account.getProfile());
        dto.setRole(account.getRole());
        String avatarUrl = MedialUtils.converMediaNametoMedialUrl(dto.getProfile().getAvatarUrl(), "IMAGE", serverUrl);
        if (dto.getProfile().getAvatarUrl() != null) {
            if(account.getGoogleId() == null) {
                dto.getProfile().setAvatarUrl(avatarUrl);
            }
        }
        if(account.getBranch() != null){
            dto.setBranch(account.getBranch().toDto(account.getBranch()));
        }
        return dto;
    }

}
