package org.eihq.quiltshow.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quilts")
@Data
@NoArgsConstructor
public class Quilt {
    
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;

    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = false,
        fetch = FetchType.EAGER
    )
    private List<Tag> tags = new ArrayList<>();

    private int length;
    private int width;

    @ManyToOne
    private Person piecedBy;
    
    @ManyToOne(optional = true)
    private Person quiltedBy;


    /**
     * Calculates the surface area of the quilt
     * @return
     */
    public int area() {
        return length * width;
    }
}
