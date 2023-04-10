package org.eihq.quiltshow.repository;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.eihq.quiltshow.model.Award;
import org.eihq.quiltshow.model.Category;
import org.eihq.quiltshow.model.GroupSize;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.SearchField;
import org.eihq.quiltshow.model.SearchField.MatchType;
import org.eihq.quiltshow.model.Tag;
import org.eihq.quiltshow.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuiltSearchBuilder {

	@Autowired
	ShowService showService;

	@PersistenceContext
	private EntityManager entityManager;


	/**
	 * A basic text search, matches the provided text against
	 *   - quilt name
	 *   - description
	 *   - enteredBy name
	 *   - additional quilters
	 * @param searchText
	 * @return
	 */
	public TypedQuery<Quilt> buildBasicSearch(String searchText) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Quilt> query = cb.createQuery(Quilt.class);
		Root<Quilt> quiltRoot = query.from(Quilt.class);

		List<Predicate> predicates = new ArrayList<>();
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("name"), new SearchField(searchText, MatchType.CONTAINS, null)));
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("description"), new SearchField(searchText, MatchType.CONTAINS, null)));
		addPredicate(predicates, fieldMatchesPerson(cb, quiltRoot.get("enteredBy"), new SearchField(searchText, MatchType.CONTAINS, null)));
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("additionalQuilters"), new SearchField(searchText, MatchType.CONTAINS, null)));

		query
		.select(quiltRoot)
		.where(cb.or(predicates.toArray(new Predicate[predicates.size()])));

		query.orderBy(Arrays.asList("enteredBy", "name", "description", "additionalQuilters").stream().map(o -> sortField(cb, quiltRoot, o)).collect(Collectors.toList()));

		return entityManager.createQuery(query);
	}


	public TypedQuery<Quilt> buildSearch(Report report) {


		// 		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("designSourceTypes"), search.getDesignSourceTypes()));
		//		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("designSourceName"), search.getDesignSourceName()));

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Quilt> query = cb.createQuery(Quilt.class);
		Root<Quilt> quiltRoot = query.from(Quilt.class);

		query
		.select(quiltRoot)
		.where(cb.and(buildSearchCriteria(cb, quiltRoot, query, report)));

		query.orderBy(report.getSortOrder().stream().map(o -> sortField(cb, quiltRoot, o)).collect(Collectors.toList()));

		return entityManager.createQuery(query);
	}


	public TypedQuery<Object[]> buildCountSearch(Report report) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = criteriaBuilder.createQuery(Object[].class);
		Root<Quilt> quiltRoot = query.from(Quilt.class);

		query.groupBy(report.getFields().stream()
				.filter(f -> !"count".equalsIgnoreCase(f))
				.map(f -> quiltRoot.get(f))
				.collect(Collectors.toList())
				);

		query.multiselect(report.getFields().stream()
				.map(f -> "count".equalsIgnoreCase(f) ? criteriaBuilder.count(quiltRoot) : quiltRoot.get(f))
				.collect(Collectors.toList())
				);
		
		query.where(criteriaBuilder.and(buildSearchCriteria(criteriaBuilder, quiltRoot, query, report)));

		TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);

		return typedQuery;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Predicate[] buildSearchCriteria(CriteriaBuilder cb, Root<Quilt> quiltRoot, CriteriaQuery query, Report report) {
		List<Predicate> predicates = new ArrayList<>();
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("name"), report.getName()));
		addPredicate(predicates, fieldMatchesNumber(cb, quiltRoot.get("number"), report.getNumber()));
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("description"), report.getDescription()));
		addPredicate(predicates, fieldMatchesNumber(cb, quiltRoot.get("length"), report.getLength()));
		addPredicate(predicates, fieldMatchesNumber(cb, quiltRoot.get("width"), report.getWidth()));
		addPredicate(predicates, fieldMatchesBoolean(cb, quiltRoot.get("judged"), report.getJudged()));
		addPredicate(predicates, fieldMatchesBoolean(cb, quiltRoot.get("firstShow"), report.getFirstEntry()));
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("mainColor"), report.getMainColor()));
		addPredicate(predicates, fieldMatchesNumber(cb, quiltRoot.get("hangingPreference"), report.getHangingPreference()));
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("mainColor"), report.getMainColor()));
		addPredicate(predicates, fieldMatchesString(cb, quiltRoot.get("additionalQuilters"), report.getAdditionalQuilters()));
		addPredicate(predicates, fieldMatchesCategories(cb, quiltRoot.get("category"), report.getCategory()));
		addPredicate(predicates, fieldMatchesTags(query, quiltRoot, cb, quiltRoot.get("tags"), report.getTags()));
		addPredicate(predicates, fieldMatchesGroupSize(cb, quiltRoot.get("groupSize"), report.getGroupSize()));

		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private void addPredicate(List<Predicate> predicates, Predicate p) {
		if(p != null) {
			predicates.add(p);
		}
	}

	/**
	 * Matches the search filter as a string. The filter can specify an exact match (will still ignore case),
	 * or it will default to matching strings that contains the filter text (ignoring case) 
	 * If the filter is empty or missing, ignores this field
	 * @param cb 
	 * @param field
	 * @param filter
	 * @return
	 */
	private Predicate fieldMatchesString(CriteriaBuilder cb, Path<String> field, SearchField filter) {
		if(filter == null || filter.isEmpty()) {
			return null;
		}

		if(filter.getMatchType() == MatchType.EQUALS) {
			return cb.equal(cb.upper(field), filter.getMatches().toUpperCase());
		}
		else {
			return cb.like(cb.upper(field), contains(filter.getMatches().toUpperCase()));
		}
	}

	private String contains(String value) {
		return MessageFormat.format("%{0}%", value);
	}

	/**
	 * Matches the search filter as a boolean value
	 * If the filter is empty or missing, ignores this field
	 * @param cb
	 * @param field
	 * @param filter
	 * @return
	 */
	private Predicate fieldMatchesBoolean(CriteriaBuilder cb, Path<Boolean> field, SearchField filter) {
		if(filter == null || filter.isEmpty()) {
			return null;
		}

		return cb.equal(field, filter.getMatchesBoolean());
	}

	/**
	 * Matches the search filter as a numeric value. The filter can specify greater than/equal, less than/equal, 
	 * between two values or it will default to matching on equal values
	 * If the filter is empty or missing, ignores this field
	 * @param cb
	 * @param field
	 * @param filter
	 * @return
	 */
	private Predicate fieldMatchesNumber(CriteriaBuilder cb, Path<Double> field, SearchField filter) {
		if(filter == null || filter.isEmpty()) {
			return null;
		}

		if(filter.getMatchType() == MatchType.GREATER_THAN) {
			return cb.ge(field, filter.getMatchesNumber());
		}
		else if(filter.getMatchType() == MatchType.LESS_THAN) {
			return cb.le(field, filter.getMatchesNumber());
		}
		else if(filter.getMatchType() == MatchType.BETWEEN) {
			return cb.between(field, filter.getMatchesRangeMin(), filter.getMatchesRangeMax());
		}
		else {
			return cb.equal(field, filter.getMatchesNumber());
		}
	}


	private Predicate fieldMatchesPerson(CriteriaBuilder cb, Path<Person> field, SearchField filter) {
		if(filter == null || filter.isEmpty()) {
			return null;
		}

		if(filter.getMatchType() == MatchType.EQUALS) {
			return cb.equal(cb.concat(cb.concat(field.get("firstName"), " "), field.get("lastName")), filter.getMatches());
		}
		else {
			return cb.or(fieldMatchesString(cb, field.get("firstName"), filter), fieldMatchesString(cb, field.get("lastName"), filter));
		}
	}

	private Predicate fieldMatchesGroupSize(CriteriaBuilder cb, Path<GroupSize> field, SearchField filter) {
		if(filter == null || filter.isEmpty()) {
			return null;
		}

		return field.in(groupSizeList(filter.getMatchesStringList()));
	}

	private Predicate fieldMatchesCategories(CriteriaBuilder cb, Path<Category> field, SearchField filter) {
		if(filter == null || filter.isEmpty()) {
			return null;
		}

		if(filter.getMatchType() == MatchType.ALL_OF) {
			return null;
		}
		else {
			return field.in(categoryList(filter.getMatchesLongList()));
		}
	}

	private Predicate fieldMatchesTags(CriteriaQuery<Quilt> query, Root<Quilt> quiltRoot, CriteriaBuilder cb, Path<Tag> field, SearchField filter) {
		if(filter == null || filter.isEmpty()) {
			return null;
		}

		if(filter.getMatchType() == MatchType.ALL_OF) {
			return null;
		}
		else {
			query.distinct(true);
			Subquery<Tag> tagQuery = query.subquery(Tag.class);
			Root<Tag> tagRoot = tagQuery.from(Tag.class);
			Expression<Collection<Quilt>> taggedQuilts = tagRoot.get("quilts");
			tagQuery.select(tagRoot);
			tagQuery.where(tagRoot.in(tagList(filter.getMatchesLongList())), cb.isMember(quiltRoot, taggedQuilts));
			return cb.exists(tagQuery);
		}
	}


	private List<GroupSize> groupSizeList(List<String> sizes) {
		return sizes.stream().map(s -> GroupSize.from(s)).collect(Collectors.toList());
	}


	private List<Category> categoryList(List<Long> ids) {
		return ids.stream().map(id -> {
			Category c = new Category();
			c.setId(id);
			return c;
		}).collect(Collectors.toList());
	}

	private List<Tag> tagList(List<Long> ids) {
		return ids.stream().map(id -> {
			Tag t = new Tag();
			t.setId(id);
			return t;
		}).collect(Collectors.toList());
	}	

	private List<Award> awardList(List<Long> ids) {
		return ids.stream().map(id -> {
			Award a = new Award();
			a.setId(id);
			return a;
		}).collect(Collectors.toList());
	}

	/**
	 * Creates the sort criteria based on the type of the field 
	 * @param cb
	 * @param quiltRoot
	 * @param field
	 * @return
	 */
	private Order sortField(CriteriaBuilder cb, Root<Quilt> quiltRoot, String field) {
		try {
			Field f = Quilt.class.getDeclaredField(field);
			if(f.getType().equals(String.class)) {
				return cb.asc(cb.upper(quiltRoot.get(field)));
			}

			if(Number.class.isAssignableFrom(f.getType()) 
					|| Boolean.class.equals(f.getType())) {
				return cb.asc(quiltRoot.get(field));
			}

			if(Category.class.equals(f.getType())) {
				Path<Category> path = quiltRoot.get(field);
				return cb.asc(cb.upper(path.get("name")));
			}

			if(Person.class.equals(f.getType())) {
				Path<Person> path = quiltRoot.get(field);
				Path<String> lastName = path.get("lastName");
				return cb.asc(cb.upper(lastName));
			}

		} catch (NoSuchFieldException | SecurityException e) {
			// use the default sort instead
		}

		// default to standard sort on the date entered
		return cb.asc(quiltRoot.get("submittedOn"));
	}

	//*/
}
