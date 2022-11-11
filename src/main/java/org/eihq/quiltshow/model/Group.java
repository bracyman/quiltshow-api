package org.eihq.quiltshow.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
public class Group {
    
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = false,
        fetch = FetchType.EAGER
    )
    private List<Tag> tags = new ArrayList<>();
}
