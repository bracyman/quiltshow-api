package org.eihq.quiltshow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "quilts")
@Data
@NoArgsConstructor
public class Quilt {
 
	@Id
    @GeneratedValue
    private Long id;

    @GeneratedValue(generator = "quilt-number-generator")
    @GenericGenerator(name = "quilt-number-generator",
    	strategy = "org.eihq.quiltshow.repository.QuiltNumberGenerator")
    private Integer number;

	@JsonIgnore
    @ManyToOne
    @EqualsAndHashCode.Exclude 
	@ToString.Exclude
	private Show show;
	
    private String name;
    
	@Column(length=5000)
    private String description;
    
    @ManyToOne
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
    		name="quilt_tags",
    		joinColumns = @JoinColumn(name = "quilt_id"),
    		inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonIncludeProperties({"id","name","description"})
	private List<Tag> tags = new ArrayList<>();
    
    private Boolean presidentsChallenge;
    private Boolean firstEntry;

    private Double length;
    private Double width;
    private Boolean firstShow;
    private Boolean judged;

    private GroupSize groupSize;
    
    private String mainColor;
    
    private int hangingPreference;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "design_source_id", referencedColumnName = "id")
    private DesignSource designSource;
    
    @ManyToOne
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
    private Person enteredBy;
   
    private String quiltedBy;
    private String additionalQuilters;
    
    private LocalDateTime submittedOn = LocalDateTime.now();
    
    private LocalDateTime lastUpdatedOn = LocalDateTime.now();
    
    
    @ManyToMany
    @JoinTable(
    		name="awards_assigned",
    		joinColumns = @JoinColumn(name = "quilt_id"),
    		inverseJoinColumns = @JoinColumn(name = "award_id"))
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
    private Set<Award> awards = new HashSet<>();    
    
    @ManyToOne
    @EqualsAndHashCode.Exclude
	@ToString.Exclude
    private PaymentData paymentData;
    

    /**
     * Calculates the surface area of the quilt
     * @return
     */
    public double area() {
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
