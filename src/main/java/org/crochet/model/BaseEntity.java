package org.crochet.model;

import com.github.f4b6a3.tsid.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity extends AuditTable {
    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    private String id;

    @PrePersist
    public void init() {
        this.id = Tsid.fast().toString();
    }
}
