package org.eihq.quiltshow.repository;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.eihq.quiltshow.model.Award;
import org.eihq.quiltshow.model.Category;
import org.eihq.quiltshow.model.GroupSize;
import org.eihq.quiltshow.model.Person;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.QuiltSearchData;
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
	

	public List<Object> executeQuery(String query) {
		Query typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}
	
	public List<QuiltSearchResult> executeQueryReport(Report report) {
		if(report.getFields().contains("count")) {
			return executeCountingQueryReport(report);
		}
		
		List<String> wheres = new ArrayList<>();
		Map<String,Object> params = new HashMap<>();		
		buildSearchFilters(report, wheres, params);
		
		StringBuilder jpql = new StringBuilder()
				.append("select qsd from QuiltSearchData qsd ");
		
		if(wheres.size() > 0) {
			jpql.append("where ").append(wheres.stream().collect(Collectors.joining(" and ")));
		}
		
		if(report.getSortOrder().stream().filter(so -> !StringUtils.isBlank(so)).count() > 0) {
			jpql.append(" order by ")
				.append(report.getSortOrder().stream().filter(so -> !StringUtils.isBlank(so)).map(f -> getSortField(f)).collect(Collectors.joining(",")));
		}
		
		
		TypedQuery<QuiltSearchData> typedQuery = entityManager.createQuery(jpql.toString(), QuiltSearchData.class);
		params.forEach((k, v) -> typedQuery.setParameter(k, v));
		List<QuiltSearchData> results = typedQuery.getResultList();
		
		List<QuiltSearchResult> searchResults = new ArrayList<>();
		results.forEach(row -> searchResults.add(new QuiltSearchResult(row)));
		
		return searchResults;
	}

	
	public List<QuiltSearchResult> executeCountingQueryReport(Report report) {
		List<String> wheres = new ArrayList<>();
		Map<String,Object> params = new HashMap<>();		
		buildSearchFilters(report, wheres, params);
		
		List<String> fields = report.getFields().stream()
				.filter(f -> (!f.equals("count") && !f.equals("tags")))
				.collect(Collectors.toList());
		StringBuilder jpql = new StringBuilder().append("select count(qsd)");
		fields.stream().forEach(f -> jpql.append(", qsd.quilt.").append(f));
		jpql.append(" from QuiltSearchData qsd ");
		
		if(wheres.size() > 0) {
			jpql.append("where ").append(wheres.stream().collect(Collectors.joining(" and ")));
		}
		
		jpql.append(" group by ")
			.append(fields.stream()
					.map(f -> String.format("qsd.quilt.%s", f))
					.collect(Collectors.joining(", ")));
		
		Query typedQuery = entityManager.createQuery(jpql.toString());
		params.forEach((k, v) -> typedQuery.setParameter(k, v));
		List<Object[]> results = typedQuery.getResultList();		
		
		List<QuiltSearchResult> searchResults = new ArrayList<>();
		results.forEach(row -> {
			QuiltSearchData qsd = new QuiltSearchData();
			for(int i = 0; i < fields.size(); i++) {
				qsd.set(fields.get(i), row[i+1]);
			}
			searchResults.add(new QuiltSearchResult(qsd, (Long)row[0]));	
		});
		
		return searchResults;
	}

	private void buildSearchFilters(Report report, List<String> wheres, Map<String,Object> params) {	
		addStringMatch(report.getName(), "name", wheres, params);
		addNumberMatch(report.getNumber(), "number", wheres, params);
		addNumberMatch(report.getWidth(), "width", wheres, params);
		addNumberMatch(report.getLength(), "length", wheres, params);
		addStringMatch(report.getDescription(), "description", wheres, params);
		addCategoryMatch(report.getCategory(), "category", wheres, params);
		addTagMatch(report.getTags(), "tags", wheres, params);
		addBooleanMatch(report.getJudged(), "judged", wheres, params);
		addGroupSizeMatch(report.getGroupSize(), "groupSize", wheres, params);
//		addPersonMatch(report.getEnteredBy(), "enteredBy", wheres, params);
		addStringMatch(report.getAdditionalQuilters(), "additionalQuilters", wheres, params);
		addBooleanMatch(report.getFirstEntry(), "firstEntry", wheres, params);
		addNumberMatch(report.getHangingPreference(), "hangingPreference", wheres, params);
		addStringMatch(report.getMainColor(), "mainColor", wheres, params);
		addBooleanMatch(report.getPresidentsChallenge(), "presidentsChallenge", wheres, params);
		addNotNullMatch(report.getJudgeComment(), "judgeComment", wheres, params);
		addNotNullMatch(report.getAwards(), "awards", wheres, params);
	}

	private boolean tagsMatch(List<Tag> quiltTags, SearchField searchField) {
		if((quiltTags == null) || quiltTags.isEmpty()) {
			return false;
		}
		
		if(StringUtils.isEmpty(searchField.getMatches())) {
			return true;
		}
		
		MatchType matchType = searchField.getMatchType() == null ? MatchType.ONE_OF : searchField.getMatchType();
		List<String> searchIds = Arrays.asList(searchField.getMatches().split(","));
		if(matchType == MatchType.ONE_OF) {
			if(quiltTags.stream().anyMatch(t -> searchIds.contains(t.getId().toString()))) {
				return true;
			}
			return false;
		}

		if(matchType == MatchType.ALL_OF) {
			if(quiltTags.stream().allMatch(t -> searchIds.contains(t.getId().toString()))) {
				return true;
			}
			return false;
		}

		return false;
	}

	private void addBooleanMatch(SearchField searchField, String field, List<String> wheres, Map<String,Object> params) {
		if(searchField == null) {
			return;
		}
		
		if(!StringUtils.isEmpty(searchField.getMatches())) {
			if(searchField.getMatchesBoolean() == Boolean.FALSE) {
				wheres.add("(qsd.quilt." + field + " != TRUE)");
			}
			else {
				wheres.add("(qsd.quilt." + field + " = TRUE)");
			}
		}
	}

	private void addStringMatch(SearchField searchField, String field, List<String> wheres, Map<String,Object> params) {
		if(searchField == null) {
			return;
		}
		
		if(!StringUtils.isEmpty(searchField.getMatches())) {
			if(searchField.getMatchType() == SearchField.MatchType.EQUALS) {
				wheres.add("(upper(qsd.quilt." + field + ") = upper(:" + field + "))");
				params.put(field, searchField.getMatches());
			}
			else {
				wheres.add("(upper(qsd.quilt." + field + ") like :" + field + ")");
				params.put(field, "%" + searchField.getMatches().toUpperCase() + "%");
			}
		}
	}

	private void addGroupSizeMatch(SearchField searchField, String field, List<String> wheres, Map<String,Object> params) {
		if(searchField == null) {
			return;
		}
		
		if(!StringUtils.isEmpty(searchField.getMatches())) {
			String sizes[] = searchField.getMatches().split(",");
			StringBuilder where = new StringBuilder("(");
			String separator = "";
			
			for(int i=0; i < sizes.length; i++) {
				int size = GroupSize.from(sizes[i]).ordinal();
				where.append(separator).append("(qsd.quilt." + field + " = " + size + ")");
				separator = " or ";
			}
			
			where.append(")");
			wheres.add(where.toString());
		}
	}
	
	private void addNumberMatch(SearchField searchField, String field, List<String> wheres, Map<String,Object> params) {
		if(searchField == null) {
			return;
		}
		
		if(!StringUtils.isEmpty(searchField.getMatches())) {
			if((searchField.getMatchType() == null) || (searchField.getMatchType() == SearchField.MatchType.EQUALS)) {
				if(searchField.getMatches().contains(",")) {
					wheres.add("(qsd.quilt." + field + " in :" + field + ")");
					params.put(field, searchField.getMatchesIntegerList());
				}
				else {
					wheres.add("(qsd.quilt." + field + " = :" + field + ")");
					params.put(field, searchField.getMatchesInt());
				}
			}
			else if(searchField.getMatchType() == SearchField.MatchType.LESS_THAN) {
				wheres.add("(qsd.quilt." + field + " < :" + field + ")");
				params.put(field, searchField.getMatchesInt());
			}
			else if(searchField.getMatchType() == SearchField.MatchType.GREATER_THAN) {
				wheres.add("(qsd.quilt." + field + " > :" + field + ")");
				params.put(field, searchField.getMatchesInt());
			}
			else if(searchField.getMatchType() == SearchField.MatchType.BETWEEN) {
				wheres.add("(qsd.quilt." + field + " >= :" + field + "RangeMin" + " and q." + field + " <= :" + field + "RangeMax)");

				params.put(field + "RangeMin", searchField.getMatchesRangeMin());
				params.put(field + "RangeMax", searchField.getMatchesRangeMax());
			}
		}
	}
	
	private void addNotNullMatch(SearchField searchField, String field, List<String> wheres, Map<String,Object> params) {
		if(searchField == null) {
			return;
		}
		
		if(field.equals("tags") || field.equals("awards")) {
			wheres.add("(not qsd.quilt." + field + " is empty)");
		}
		else {
			wheres.add("(not qsd.quilt." + field + " is null)");
		}
	}

	private void addCategoryMatch(SearchField searchField, String field, List<String> wheres, Map<String,Object> params) {
		if(searchField == null) {
			return;
		}
		
		if(!StringUtils.isEmpty(searchField.getMatches())) {
			List<String> values = Arrays.asList(searchField.getMatches().split(",")); 
			wheres.add(String.format("(qsd.quilt.%s IN :%s)", field, field));
			params.put(field, values.stream().map(c -> new Category(Long.parseLong(c))).collect(Collectors.toList()));
		}
	}

	private void addTagMatch(SearchField searchField, String field, List<String> wheres, Map<String,Object> params) {
		if(searchField == null) {
			return;
		}
		
		if(!StringUtils.isEmpty(searchField.getMatches())) {
			String separator = searchField.getMatchType() == MatchType.ALL_OF ? " and " : " or ";
			List<String> values = Arrays.asList(searchField.getMatches().split(","));
			
			StringBuilder tagFilter = new StringBuilder("(");
			for(int i = 0; i < values.size(); i++) {
				String fieldName = String.format("tag_value_%d", i);
				tagFilter.append(String.format("%s(:%s MEMBER OF qsd.quilt.tags)", i > 0 ? separator : "", fieldName));
				
				Tag t = new Tag();
				t.setId(Long.valueOf(values.get(i)));
				params.put(fieldName, t);
			}
			tagFilter.append(")");
			wheres.add(tagFilter.toString());
		}
	}
	
	private String getSortField(String field) {
		if("category".equals(field)) return "qsd.quilt.category.displayOrder, qsd.quilt.category.name";
		if("enteredBy".equals(field)) return "qsd.quilt.enteredBy.lastName, qsd.quilt.enteredBy.firstName";
		
		return String.format("qsd.quilt.%s", field);
	}
}