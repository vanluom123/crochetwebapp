package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "banner")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Banner extends BaseEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "url")
    private String url;

    @Column(name = "active", columnDefinition = "boolean default false")
    private boolean active;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_content")
    private String fileContent;

    @Column(name = "text_color")
    private String textColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_type_id", referencedColumnName = "id", nullable = false)
    private BannerType bannerType;
}
