package org.eihq.quiltshow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

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

    private Integer length;
    private Integer width;
    private Boolean judged;

    private QuiltEffort effort;
    
    @JsonBackReference
    @ManyToOne
    private Person enteredBy;
   
    private String additionalQuilters;
    
    private LocalDateTime submittedOn = LocalDateTime.now();
    
    private LocalDateTime lastUpdatedOn = LocalDateTime.now();
    
    

    /**
     * Calculates the surface area of the quilt
     * @return
     */
    public int area() {
        return length * width;
    }
    
    /**
     * Returns additionalQuilters as a list of quilter names 
     */
    public List<String> getAdditionalQuilters() {
    	return (additionalQuilters == null) ? Collections.emptyList() : Arrays.asList(additionalQuilters.split(","));
    }
    
    public void setAdditionalQuilters(List<String> additionalQuilters) {
    	this.additionalQuilters = additionalQuilters.stream().collect(Collectors.joining(","));
    }
}
