package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.request.ConsignmentRequestMediaResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consignment_request_media",
        indexes = {
                @Index(name = "idx_crm_request", columnList = "request_id")
        })
public class ConsignmentRequestMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> consignment_request(id)
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false,
            foreignKey = @ForeignKey(name = "fl_crm_request"))
    private ConsignmentRequest request;

    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 16)
    private MediaType mediaType = MediaType.IMAGE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public ConsignmentRequestMediaResponseDTO toDto(ConsignmentRequestMedia media) {
        ConsignmentRequestMediaResponseDTO dto = new ConsignmentRequestMediaResponseDTO();
        dto.setId(media.getId());
        dto.setMediaType(media.getMediaType());
        dto.setMediaUrl(media.getMediaUrl());
        return dto;
    }
}
