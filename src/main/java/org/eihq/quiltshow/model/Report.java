package org.eihq.quiltshow.model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Tag> tags;

    @ElementCollection
    private List<String> sortFields;
}
