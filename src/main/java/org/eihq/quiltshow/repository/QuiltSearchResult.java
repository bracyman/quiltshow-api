package org.eihq.quiltshow.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eihq.quiltshow.model.Award;
import org.eihq.quiltshow.model.Category;
import org.eihq.quiltshow.model.DesignSource;
import org.eihq.quiltshow.model.GroupSize;
import org.eihq.quiltshow.model.HangingLocation;
import org.eihq.quiltshow.model.HangingUnit;
import org.eihq.quiltshow.model.HangingUnit.Types;
import org.eihq.quiltshow.model.JudgeComment;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.QuiltSearchData;
import org.eihq.quiltshow.model.Tag;
import org.eihq.quiltshow.model.TagCategory;
import org.eihq.quiltshow.model.Wall;

import lombok.Data;

@Data
public class QuiltSearchResult {

	QuiltResult quilt;
	LocationResult hangingLocation;
	Long count;
	
	public QuiltSearchResult(QuiltSearchData qsd) {
		if(qsd != null) {
			quilt = (qsd.getQuilt() == null) ? null : new QuiltResult(qsd.getQuilt());
			hangingLocation = (qsd.getHangingLocation() == null) ? null : new LocationResult(qsd.getHangingLocation());
			this.count = 1l;
		}
	}
	
	public QuiltSearchResult(QuiltSearchData qsd, Long count) {
		if(qsd != null) {
			quilt = (qsd.getQuilt() == null) ? null : new QuiltResult(qsd.getQuilt());
			hangingLocation = (qsd.getHangingLocation() == null) ? null : new LocationResult(qsd.getHangingLocation());
			this.count = count;
		}
	} 
}

@Data
class QuiltResult {
	Long id;
	Integer number;
	String name;
	String description;
	CategoryResult category;
	List<TagResult> tags;
	Boolean presidentsChallenge;
    Boolean firstEntry;

    Double length;
    Double width;
    Boolean firstShow;
    Boolean judged;

    GroupSize groupSize;
    
    String mainColor;
    
    int hangingPreference;
    DesignSource designSource;
    Person enteredBy;
    
    String quiltedBy;
    String additionalQuilters;
    
    LocalDateTime submittedOn;    
    LocalDateTime lastUpdatedOn;
    Set<AwardResult> awards;
    
    JudgeComment judgeComment;
    
	public QuiltResult(Quilt quilt) {
		this.id = quilt.getId();
		this.number = quilt.getNumber();
		this.name = quilt.getName();
		this.description = quilt.getDescription();
		this.category = (quilt.getCategory() == null) ? null : new CategoryResult(quilt.getCategory());
		this.tags = quilt.getTags().stream().map(t -> (t == null) ? null : new TagResult(t)).collect(Collectors.toList());
		this.presidentsChallenge = quilt.getPresidentsChallenge();
		this.firstEntry = quilt.getFirstEntry();

		this.length = quilt.getLength();
		this.width = quilt.getWidth();
		this.firstShow = quilt.getFirstShow();
		this.judged = quilt.getJudged();

		this.groupSize = quilt.getGroupSize();
	    
		this.mainColor = quilt.getMainColor();
	    
		this.hangingPreference = quilt.getHangingPreference();
		this.designSource = quilt.getDesignSource();
		this.enteredBy = quilt.getEnteredBy();
	    
		this.quiltedBy = quilt.getQuiltedBy();
		this.additionalQuilters = quilt.getAdditionalQuilters();
	    
		this.submittedOn = quilt.getSubmittedOn(); 
		this.lastUpdatedOn = quilt.getLastUpdatedOn();
		this.awards = quilt.getAwards().stream().map(a -> (a == null) ? null : new AwardResult(a)).collect(Collectors.toSet());
		
		this.judgeComment = quilt.getJudgeComment();
	}
}

@Data
class LocationResult {
	Long id;
	Map<String, Double> location;
	WallResult wall;
	Quilt quilt;
	
	public LocationResult(HangingLocation location) {
		this.id = location.getId();
		this.location = location.getLocation();
		this.wall = (location.getWall() == null) ? null : new WallResult(location.getWall());
	}
}

@Data
class WallResult {
	Long id;
	String name;
	Double width;
	Double height;
	HangingUnitResult hangingUnit;
	public WallResult(Wall w) {
		this.id = w.getId();
		this.name = w.getName();
		this.width = w.getWidth();
		this.height = w.getHeight();
		this.hangingUnit = (w.getHangingUnit() == null) ? null : new HangingUnitResult(w.getHangingUnit());
	}
}

@Data
class HangingUnitResult {
    Long id;
    Types unitType;
	String name;
	Map<String, Double> location;
	Map<String, Double> size;
	
	public HangingUnitResult(HangingUnit hu) {
		this.id = hu.getId();
		this.unitType = hu.getUnitType();
		this.name = hu.getName();
		this.location = hu.getLocation();
		this.size = hu.getSize();
	}
}

@Data
class CategoryResult {
	Long id;
	String name;
	String shortDescription;
	String description;
	int displayOrder;
	Boolean judgeable;
	
	public CategoryResult(Category c) {
		this.id = c.getId();
		this.name = c.getName();
		this.shortDescription = c.getShortDescription();
		this.description = c.getDescription();
		this.displayOrder = c.getDisplayOrder();
		this.judgeable = c.getJudgeable();
	}
}

@Data
class TagResult {
	Long id;
	String name;
	String description;
	TagCategoryResult tagCategory;

	public TagResult(Tag t) {
		this.id = t.getId();
		this.name = t.getName();
		this.description = t.getDescription();
		this.tagCategory = (t.getTagCategory() == null) ? null : new TagCategoryResult(t.getTagCategory());
	}
}

@Data
class TagCategoryResult {
	Long id;
	String name;
	String description;
	Boolean onlyOne;
	Boolean requireOne;
	
	public TagCategoryResult(TagCategory tc) {
		this.id = tc.getId();
		this.name = tc.getName();
		this.description = tc.getDescription();
		this.onlyOne = tc.getOnlyOne();
		this.requireOne = tc.getRequireOne();
	}
}

@Data
class AwardResult {
	private Long id;
	private String name;
	private String description;
	Integer displayOrder;
	CategoryResult category;

	public AwardResult(Award a) {
		this.id = a.getId();
		this.name = a.getName();
		this.description = a.getDescription();
		this.displayOrder = a.getDisplayOrder();
		this.category = a.getCategory() == null ? null : new CategoryResult(a.getCategory());
	}
}