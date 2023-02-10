package org.eihq.quiltshow.repository;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.eihq.quiltshow.model.Category;
import org.eihq.quiltshow.model.DesignSourceType;
import org.eihq.quiltshow.model.GroupSize;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Tag;
import org.springframework.data.jpa.domain.Specification;

import lombok.Data;


@Data
public class QuiltSearch {

	private String name = null;

	private String description = null;

	private List<Category> categories = new LinkedList<>();

	private List<Tag> tags = new LinkedList<>();

	private Integer length = null;
	private Integer maxLength = null;
	private Integer minLength = null;

	private Integer width = null;
	private Integer maxWidth = null;
	private Integer minWidth = null;

	private Boolean firstShow = null;
	private Boolean judged = null;

	private GroupSize groupSize = null;

	private String mainColor = null;

	private Integer hangingPreference = null;
	private Integer maxHangingPreference = null;
	private Integer minHangingPreference = null;

	private List<DesignSourceType> designSourceTypes = new LinkedList<>();

	private String designSourceName = null;

	private Person enteredBy = null;

	private String additionalQuilters = null;


	public Specification<Quilt> buildSearch() {
		Specification<Quilt> spec = Specification
				.where(name == null ? null : fieldContains("name", name))
				.and(description == null ? null : fieldContains("description", description))
				.and(categories.isEmpty() ? null : oneOfCategories(categories))
				.and(tags.isEmpty() ? null : containsAllTags(tags))
				.and(mainColor == null ? null : fieldContains("mainColor", mainColor))
				.and(fieldMatchesOrInRange("length", length, minLength, maxLength))
				.and(fieldMatchesOrInRange("width", width, minWidth, maxWidth))
				;

		return spec;
	}

	private static Specification<Quilt> fieldContains(String fieldName, String value) {
		return (root, query, builder) -> builder.like(root.get(fieldName), contains(value));
	}

	private static String contains(String value) {
		return MessageFormat.format("%{0}%", value);
	}

	private static Specification<Quilt> oneOfCategories(List<Category> categories) {
		return (root, query, builder) -> {
			final Path<Category> category = root.<Category> get("category");
			return category.in(categories);
		};
	}

	private static Specification<Quilt> containsAllTags(List<Tag> tags) {
		return (root, query, builder) -> {
			query.distinct(true);
			Subquery<Tag> tagQuery = query.subquery(Tag.class);
			Root<Tag> tagRoot = tagQuery.from(Tag.class);
			Expression<Collection<Quilt>> taggedQuilts = tagRoot.get("quilts");
			tagQuery.select(tagRoot);
			tagQuery.where(tagRoot.in(tags), builder.isMember(root, taggedQuilts));
			return builder.exists(tagQuery);
		};
	}

	private static Specification<Quilt> fieldMatchesOrInRange(String fieldName, Integer match, Integer min, Integer max) {
		if(match != null) {
			return fieldEquals(fieldName, match);
		}
		else if((min != null) && (max != null)) {
			return fieldBetween(fieldName, min, max);
		}
		else if((min != null) && (max == null)) { 
			return fieldGreaterThanOrEqual(fieldName, min);
		}
		else if((min == null) && (max != null)) { 
			return fieldLessThanOrEqual(fieldName,  max);
		}
		
		return null;
	}

	private static Specification<Quilt> fieldEquals(String fieldName, Integer value) {
		return (root, query, builder) -> builder.equal(root.get(fieldName), value);
	}    

	private static Specification<Quilt> fieldBetween(String fieldName, Integer min, Integer max) {
		return (root, query, builder) -> builder.between(root.get(fieldName), min, max);
	}

	private static Specification<Quilt> fieldLessThanOrEqual(String fieldName, Integer max) {
		return (root, query, builder) -> builder.le(root.get(fieldName), max);
	}

	private static Specification<Quilt> fieldGreaterThanOrEqual(String fieldName, Integer min) {
		return (root, query, builder) -> builder.ge(root.get(fieldName), min);
	}


	/* Builder functions */

	public QuiltSearch name(String name) {
		this.name = name;
		return this;
	}

	public QuiltSearch description(String description) {
		this.description = description;
		return this;
	}

	public QuiltSearch categories(List<Category> categories) {
		this.categories = categories;
		return this;
	}

	public QuiltSearch addCategory(Category category) {
		this.categories.add(category);
		return this;
	}

	public QuiltSearch tags(List<Tag> tags) {
		this.tags = tags;
		return this;
	}

	public QuiltSearch addTag(Tag tag) {
		this.tags.add(tag);
		return this;
	}

	public QuiltSearch length(Integer length) {
		this.length = length;
		return this;
	}

	public QuiltSearch maxLength(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public QuiltSearch minLength(Integer minLength) {
		this.minLength = minLength;
		return this;
	}

	public QuiltSearch width(Integer width) {
		this.width = width;
		return this;
	}

	public QuiltSearch maxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public QuiltSearch minWidth(Integer minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public QuiltSearch firstShow(Boolean firstShow) {
		this.firstShow = firstShow;
		return this;
	}

	public QuiltSearch judged(Boolean judged) {
		this.judged = judged;
		return this;
	}

	public QuiltSearch groupSize(GroupSize groupSize) {
		this.groupSize = groupSize;
		return this;
	}

	public QuiltSearch mainColor(String mainColor) {
		this.mainColor = mainColor;
		return this;
	}

	public QuiltSearch hangingPreference(Integer hangingPreference) {
		this.hangingPreference = hangingPreference;
		return this;
	}

	public QuiltSearch designSourceTypes(List<DesignSourceType> designSourceTypes) {
		this.designSourceTypes = designSourceTypes;
		return this;
	}

	public QuiltSearch addDesignSourceType(DesignSourceType designSourceType) {
		this.designSourceTypes.add(designSourceType);
		return this;
	}

	public QuiltSearch designSourceName(String designSourceName) {
		this.designSourceName = designSourceName;
		return this;
	}

	public QuiltSearch enteredBy(Person enteredBy) {
		this.enteredBy = enteredBy;
		return this;
	}

	public QuiltSearch additionalQuilters(String additionalQuilters) {
		this.additionalQuilters = additionalQuilters;
		return this;
	}

}
