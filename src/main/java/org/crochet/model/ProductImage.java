package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_image")
public class ProductImage extends CrochetImage {
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Lob
    @Column(name = "imageUrl", columnDefinition = "LONGBLOB")
    @Override
    public String getImageUrl() {
        return super.getImageUrl();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Override
    public Long getImageId() {
        return super.getImageId();
    }
}
