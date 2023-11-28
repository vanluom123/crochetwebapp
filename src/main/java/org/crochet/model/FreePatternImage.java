package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "free_pattern_image")
public class FreePatternImage extends CrochetImage {
    @ManyToOne
    @JoinColumn(name = "free_pattern_id", nullable = false)
    private FreePattern freePattern;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    @Override
    public String getImage() {
        return super.getImage();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Override
    public Long getImageId() {
        return super.getImageId();
    }
}
