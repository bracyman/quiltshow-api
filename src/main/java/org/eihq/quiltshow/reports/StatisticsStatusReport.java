package org.eihq.quiltshow.reports;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eihq.quiltshow.exception.NotFoundException;
import org.eihq.quiltshow.model.GroupSize;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.model.Show;
import org.eihq.quiltshow.model.TagCategory;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.eihq.quiltshow.repository.ShowRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatisticsStatusReport extends Report {

	public static final Long ID = -20l;
	
	
	private static final String TOTAL_QUILTS = "totalQuilts";
	private static final String TOTAL_JUDGED_QUILTS = "totalJudgedQuilts";
	private static final String TOTAL_FIRST_TIME = "totalFirstTimeQuilts";
	
	private static final String NUM_ENTRANTS = "numEntrants";
	private static final String AVG_PER_ENTRANT = "avgPerEntrant";
	private static final String NUM_JUDGED_ENTRANTS = "numJudgedEntrants";
	private static final String AVG_JUDGED_PER_ENTRANT = "avgJudgedPerEntrant";
	
	private static final String ENTRIES_PER_CATEGORY = "entriesPerCategory";
	private static final String JUDGED_ENTRIES_PER_CATEGORY = "judgedEntriesPerCategory";
	private static final String ENTRIES_PER_TECHNIQUE = "entriesPerTechnique";
	private static final String ENTRIES_PER_METHOD = "entriesPerMethod";
	private static final String ENTRIES_PER_GROUP_SIZE = "entriesPerGroupSize";
	private static final String JUDGED_ENTRIES_PER_GROUP_SIZE = "judgedEntriesPerGroupSize";

	private static final String ENTRIES_PER_SPECIAL_EVENT = "entriesPerSpecialEvent";

	private static final String ENTRIES_PER_RIBBON = "entriesPerRibbon";
	private static final String NUM_RIBBONS_PER_TYPE = "numRibbonPerType";

	private static final String ENTRIES_PER_ALPHA = "entriesPerAlpha";
	
	

	public StatisticsStatusReport() {
		
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "Statistics";
	}


	@Override
	public ReportCategory getReportCategory() {
		return ReportCategory.SHOW;
	}
	
	@Override
	public String getFormat() {
		return "statistics";
	}


	@Override
	public String getReportDescription() {
		return "Collection of statistics about the quilts in the show";
	}


	@Override
	public List<String> getFields() {
		return Arrays.asList(TOTAL_QUILTS,TOTAL_JUDGED_QUILTS,TOTAL_FIRST_TIME, NUM_ENTRANTS,NUM_JUDGED_ENTRANTS,ENTRIES_PER_CATEGORY,ENTRIES_PER_TECHNIQUE,ENTRIES_PER_METHOD,ENTRIES_PER_GROUP_SIZE,JUDGED_ENTRIES_PER_GROUP_SIZE,ENTRIES_PER_SPECIAL_EVENT,ENTRIES_PER_RIBBON,NUM_RIBBONS_PER_TYPE,ENTRIES_PER_ALPHA);
	}
	
	
	public ReportResult run(QuiltRepository quiltRepository, ShowRepository showRepository) {
		log.debug("Starting Statistics Report...");
		
		ReportResult result = new ReportResult();
		result.setDemo(false);
		result.setReport(this);
		
		Map<String, Object> statistics = new HashMap<>();
		
		List<Quilt> quilts = quiltRepository.findAll();
		List<Quilt> judgedQuilts = quilts.stream().filter(q -> q.getJudged() != null && q.getJudged()).collect(Collectors.toList());
		List<Show> shows = showRepository.findAll();
		Show show = shows.stream().filter(s -> s.isActive()).findFirst().orElseThrow(() -> new NotFoundException("No active show found"));

		// Entry stats
		statistics.put(TOTAL_QUILTS, quilts.size());
		statistics.put(TOTAL_JUDGED_QUILTS, judgedQuilts.size());
		statistics.put(TOTAL_FIRST_TIME, quilts.stream().filter(q -> q.getFirstEntry() != null && q.getFirstEntry()).count());
		
		// Entrant stats
		List<String> entrants = quilts.stream().map(q -> q.getEnteredBy().getEmail()).distinct().collect(Collectors.toList());
		List<String> judgedEntrants = judgedQuilts.stream().map(q -> q.getEnteredBy().getEmail()).distinct().collect(Collectors.toList());
		statistics.put(NUM_ENTRANTS, entrants.size());
		statistics.put(AVG_PER_ENTRANT, entrants.size() == 0 ? 0 : quilts.size() / entrants.size());
		statistics.put(NUM_JUDGED_ENTRANTS, judgedEntrants.size());
		statistics.put(AVG_JUDGED_PER_ENTRANT, judgedEntrants.size() == 0 ? 0 : judgedQuilts.size() / judgedEntrants.size());
		
		// Category stats
		Map<String, Long> entriesPerCategory = new HashMap<>();
		show.getCategories().forEach(c -> {
			entriesPerCategory.put(c.getName(), quilts.stream().filter(q -> q.getCategory().getId().equals(c.getId())).count());
		});
		statistics.put(ENTRIES_PER_CATEGORY, entriesPerCategory);
		
		Map<String, Long> judgedEntriesPerCategory = new HashMap<>();
		show.getCategories().forEach(c -> {
			entriesPerCategory.put(c.getName(), judgedQuilts.stream().filter(q -> q.getCategory().getId().equals(c.getId())).count());
		});
		statistics.put(JUDGED_ENTRIES_PER_CATEGORY, judgedEntriesPerCategory);
		
		// Tag stats
		Map<String, Long> entriesPerTechnique = new HashMap<>();
		TagCategory techniqueCategory = show.getTagCategories().stream().filter(tc -> tc.getName().equalsIgnoreCase("Piecing Type")).findFirst().orElse(null);
		if(techniqueCategory != null) {
			techniqueCategory.getTags().forEach(tag -> {
				entriesPerTechnique.put(tag.getName(), quilts.stream().filter(q -> q.getTags().contains(tag)).count());
			});
			statistics.put(ENTRIES_PER_TECHNIQUE, entriesPerTechnique);
		}

		Map<String, Long> entriesPerMethod = new HashMap<>();
		TagCategory methodCategory = show.getTagCategories().stream().filter(tc -> tc.getName().equalsIgnoreCase("Quilting Style")).findFirst().orElse(null);
		if(methodCategory != null) {
			methodCategory.getTags().forEach(tag -> {
				entriesPerMethod.put(tag.getName(), quilts.stream().filter(q -> q.getTags().contains(tag)).count());
			});
			statistics.put(ENTRIES_PER_METHOD, entriesPerMethod);
		}
		
		
		// Group stats
		Map<String, Long> entriesPerGroupSize = new HashMap<>();
		for(GroupSize groupSize : GroupSize.values()) {
			entriesPerGroupSize.put(groupSize.readableName(), quilts.stream().filter(q -> q.getGroupSize() == groupSize).count());
		}
		statistics.put(ENTRIES_PER_GROUP_SIZE, entriesPerGroupSize);
		
		Map<String, Long> judgedEntriesPerGroupSize = new HashMap<>();
		for(GroupSize groupSize : GroupSize.values()) {
			judgedEntriesPerGroupSize.put(groupSize.readableName(), judgedQuilts.stream().filter(q -> q.getGroupSize() == groupSize).count());
		}
		statistics.put(JUDGED_ENTRIES_PER_GROUP_SIZE, judgedEntriesPerGroupSize);
		
		// Special events
		Map<String, Long> specialEvents = new HashMap<>();
		specialEvents.put("President's Challenge", quilts.stream().filter(q -> (q.getPresidentsChallenge() != null && q.getPresidentsChallenge()) || q.getCategory().getName().equals("President's Challenge - Not Judged")).count());
		statistics.put(ENTRIES_PER_SPECIAL_EVENT, specialEvents);
		
		// Ribbon stats
		statistics.put(ENTRIES_PER_RIBBON, "");
		statistics.put(NUM_RIBBONS_PER_TYPE, "");
		
		// Miscellaneous stats
		Map<String, Long> alphaCount = new HashMap<>();
		quilts.forEach(q -> {
			String c = q.getEnteredBy().getLastName().substring(0,1).toUpperCase();
			alphaCount.put(c, alphaCount.containsKey(c) ? alphaCount.get(c) + 1 : 1);
		});
		statistics.put(ENTRIES_PER_ALPHA, alphaCount);
		
		
		result.setResults(Collections.singletonList(statistics));
		return result;
	}

	
}

