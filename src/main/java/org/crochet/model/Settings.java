package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    @Id
    @Column(name = "setting_key", updatable = false, nullable = false, unique = true)
    String key;

    @Column(name = "value")
    String value;
}
