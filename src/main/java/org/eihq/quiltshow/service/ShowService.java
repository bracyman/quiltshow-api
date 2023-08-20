package org.eihq.quiltshow.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eihq.quiltshow.exception.CannotDeleteInUseException;
import org.eihq.quiltshow.exception.NoActiveShowException;
import org.eihq.quiltshow.exception.NotFoundException;
import org.eihq.quiltshow.model.Award;
import org.eihq.quiltshow.model.Category;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Show;
import org.eihq.quiltshow.model.Tag;
import org.eihq.quiltshow.model.TagCategory;
import org.eihq.quiltshow.repository.AwardRepository;
import org.eihq.quiltshow.repository.CategoryRepository;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.eihq.quiltshow.repository.ShowRepository;
import org.eihq.quiltshow.repository.TagCategoryRepository;
import org.eihq.quiltshow.repository.TagRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShowService {

	@Autowired
	ShowRepository showRepository;

	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	TagCategoryRepository tagCategoryRepository;
	
	@Autowired
	TagRepository tagRepository;
	
	@Autowired
	AwardRepository awardRepository;
	
	@Autowired 
	QuiltRepository quiltRepository;
	
	
	// ------------------------------------------------------------------------
	// Show functions
	// ------------------------------------------------------------------------
	public Show createShow(Show newShow) {
		Show created = showRepository.save(newShow);
		log.info("Created show {} [{}]", created.getName(), created.getId());
		return created;
	}
	
	public Show updateShow(Show updatedShow) {
		Show updated = showRepository.save(updatedShow);
		log.info("Updated show {} [{}]", updated.getName(), updated.getId());
		return updated;
	}
	
	public List<Show> getAllShows() {
		return showRepository.findAll();
	}
	
	/**
	 * Returns the current active show. If no show is active, returns the most recently 
	 * completed show.
	 * @return
	 */
	public Show getCurrentShow() {
		List<Show> shows = showRepository.findByActiveTrueOrderByEndDateDesc();
		if(shows.isEmpty()) {
			log.debug("No active show found, finding most recently completed show");
			shows = showRepository.findByEndDateBeforeOrderByEndDateDesc(new Date());
		}
		
		return shows.isEmpty() ? null : shows.get(0); 
	}
	
	public Show getActiveShow() {
		List<Show> shows = showRepository.findByActiveTrueOrderByEndDateDesc();	
		return shows.isEmpty() ? null : shows.get(0); 
	}
	
	public Show getShow(Long showId)  {
		return showRepository.findById(showId).orElseThrow(() -> new NotFoundException("Show", showId));
	}
	
	public Show getShowByYear(int year) {
		throw new NotYetImplementedException();
	}
	
	public void deleteShow(Long showId) {
		log.info("Deleting show [{}]", showId);
		showRepository.deleteById(showId);
	}
	
	
	// ------------------------------------------------------------------------
	// Category functions
	// ------------------------------------------------------------------------
	/**
	 * Adds the new category to the specified show. If no show is specified, the category is added to the currently active show
	 * @param showId
	 * @param newCategory
	 * @return
	 * @throws NoActiveShowException
	 * @throws NoSuchShowException
	 */
	public Category createCategory(Long showId, Category newCategory) throws NoActiveShowException {
		Show show = (showId == null) ? getActiveShow() : getShow(showId);
		
		if(show == null) {
			throw new NoActiveShowException();
		}
		
		return createCategory(show, newCategory);
	}	
	
	/**
	 * Adds the new category to the specified show. If no show is specified, the category is added to the currently active show
	 * @param showId
	 * @param newCategory
	 * @return
	 * @throws NoActiveShowException
	 * @throws NoSuchShowException
	 */
	public Category createCategory(Show show, Category newCategory) throws NoActiveShowException {
		newCategory.setShow(show);
		show.getCategories().add(newCategory);
		showRepository.save(show);
		
		return newCategory;
	}	
	
	public Category updateCategory(Category updatedCategory) {
		Category updated = categoryRepository.save(updatedCategory);
		return updated;
	}

	
	/**
	 * Attempts to delete the category. Throws an exception if the category is asssigned to any quilts
	 * @param categoryId
	 * @throws CannotDeleteInUseException 
	 */
	public void deleteCategory(Long categoryId) throws CannotDeleteInUseException {
		Optional<Category> category = categoryRepository.findById(categoryId);
		
		if(category.isPresent()) {
			if(quiltRepository.findByCategory(category.get()).size() > 0) {
				log.error("Cannot delete category {}: currently in use", categoryId);
				throw new CannotDeleteInUseException(category.get().getName(), Arrays.asList("Quilt"));
			}
			categoryRepository.delete(category.get());
		}
	}
	
	
	// ------------------------------------------------------------------------
	// Tag Category functions
	// ------------------------------------------------------------------------
	public TagCategory getTagCategory(Long tagCategoryId) {
		return tagCategoryRepository.findById(tagCategoryId).orElseThrow(() -> new NotFoundException("TagCategory", tagCategoryId));
	}
	
	public TagCategory createTagCategory(Long showId, TagCategory tagCategory) throws NoActiveShowException {
		Show show = (showId == null) ? getActiveShow() : getShow(showId);
		
		if(show == null) {
			throw new NoActiveShowException();
		}
		
		return createTagCategory(show, tagCategory);
	}
	
	public TagCategory createTagCategory(Show show, TagCategory tagCategory) throws NoActiveShowException {		
		show.getTagCategories().add(tagCategory);
		tagCategory.setShow(show);
		showRepository.save(show);
		
		return tagCategory;
	}
	
	public TagCategory updateTagCategory(Long tagCategoryId, TagCategory tagCategory) {
		TagCategory updateTagCategory =  tagCategoryRepository.save(tagCategory);
		
		return updateTagCategory;
	}
	
	public void deleteTagCategory(Long showId, Long tagCategoryId) throws NoActiveShowException {
		Show show = (showId == null) ? getActiveShow() : getShow(showId);
		Optional<TagCategory> tagCategory = tagCategoryRepository.findById(tagCategoryId);
		
		if(show == null) {
			throw new NoActiveShowException();
		}
		if(!tagCategory.isPresent()) {
			throw new NotFoundException("TagCategory", tagCategoryId);
		}
		
		show.getTagCategories().remove(tagCategory.get());
		showRepository.save(show);
		
		tagCategory.get().getTags().forEach(t -> deleteTag(t.getId()));
		
		tagCategoryRepository.deleteById(tagCategoryId);
	}
	
	
	// ------------------------------------------------------------------------
	// Tag functions
	// ------------------------------------------------------------------------
	/**
	 * Adds the new tag to the specified category. 
	 * @param showId
	 * @param newTag
	 * @return
	 */
	public Tag createTag(Long tagCategoryId, Tag newTag) {
		Optional<TagCategory> tagCategory = tagCategoryRepository.findById(tagCategoryId);
		
		if(!tagCategory.isPresent()) {
			throw new NotFoundException("Tag Category", tagCategoryId);
		}
		
		return createTag(tagCategory.get(), newTag);
	}	
	
	/**
	 * Adds the new tag to the specified category. 
	 * @param showId
	 * @param newTag
	 * @return
	 */
	public Tag createTag(TagCategory tagCategory, Tag newTag) {
		tagCategory.getTags().add(newTag);
		newTag.setTagCategory(tagCategory);
		tagCategoryRepository.save(tagCategory);
		
		return newTag;
	}	
	
	public Tag updateTag(Tag updatedTag) {
		Tag updated = tagRepository.save(updatedTag);
		return updated;
	}

	
	/**
	 * Delete the tag, removing the tag from all quilts first
	 * @param tagId 
	 */
	public void deleteTag(Long tagId)  {
		Optional<Tag> tag = tagRepository.findById(tagId);
		
		if(tag.isPresent()) {
			List<Quilt> taggedQuilts = quiltRepository.findQuiltsWithTag(tag.get());
			taggedQuilts.forEach(q -> {
				q.getTags().remove(tag.get());
				quiltRepository.save(q);
			});
			
			tag.get().getTagCategory().getTags().remove(tag.get());
			
			tagCategoryRepository.save(tag.get().getTagCategory());
			tagRepository.delete(tag.get());
		}
	}
	
	
	// ------------------------------------------------------------------------
	// Award functions
	// ------------------------------------------------------------------------
	public Award getAward(Long awardId) {
		return awardRepository.findById(awardId).orElseThrow(() -> new NotFoundException("Award", awardId));
	}
	
	/**
	 * Adds the new award to the specified show. If no show is specified, the award is added to the currently active show
	 * @param showId
	 * @param newAward
	 * @return
	 * @throws NoActiveShowException
	 */
	public Award createAward(Long showId, Award newAward) throws NoActiveShowException {
		Show show = (showId == null) ? getActiveShow() : getShow(showId);
		
		if(show == null) {
			throw new NoActiveShowException();
		}
	
		return createAward(show, newAward);
	}	
	
	/**
	 * Adds the new award to the specified show. If no show is specified, the award is added to the currently active show
	 * @param showId
	 * @param newAward
	 * @return
	 * @throws NoActiveShowException
	 */
	public Award createAward(Show show, Award newAward) throws NoActiveShowException {
		newAward.setShow(show);
		
		if(newAward.getCategory() != null) {
			Category category = show.getCategories().stream()
					.filter(c -> c.getId().equals(newAward.getCategory().getId()))
					.findFirst()
					.orElseThrow(() -> new NotFoundException(String.format("Unable to find category %d", newAward.getCategory().getId())));
			newAward.setCategory(category);			
		}
		
		Award savedAward = awardRepository.save(newAward);
		savedAward.setShow(show);
		show.getAwards().add(savedAward);
		showRepository.save(show);
		
		return savedAward;
	}	
	
	public Award updateAward(Award updatedAward) {
		Award updated = awardRepository.save(updatedAward);
		return updated;
	}
	
	/**
	 * Attempts to delete the award. Throws an exception if the award is asssigned to any quilts
	 * @param awardId
	 * @throws CannotDeleteInUseException 
	 */
	public void deleteAward(Long awardId) throws CannotDeleteInUseException {
		Optional<Award> award = awardRepository.findById(awardId);
		
		if(award.isPresent()) {
			if(!award.get().getAwardedTo().isEmpty()) {
				log.error("Cannot delete award {}: currently in use", awardId);
				throw new CannotDeleteInUseException(award.get().getName(), Arrays.asList("Quilt"));
			}
			awardRepository.delete(award.get());
		}
	}

	public void assignAward(Long awardId, List<Long> quiltIds) {
		Award award = getAward(awardId);
		Set<Quilt> quilts = new HashSet<>();
		
		for(Long quiltId : quiltIds) {
			quilts.add(quiltRepository.findById(quiltId).orElseThrow(() -> new NotFoundException("Quilt", quiltId)));
		}
		
		award.setAwardedTo(quilts);
		awardRepository.save(award);

		for(Quilt q : quilts) {
			q.getAwards().add(award);
			quiltRepository.save(q);
		}
	}

	public void assignAwardByQuiltNumber(Long awardId, List<Integer> quiltNumbers) {
		Award award = getAward(awardId);
		Set<Quilt> quilts = new HashSet<>();
		Set<Quilt> quiltsToUpdate = new HashSet<>();
		
		if(award.getAwardedTo() != null) {
			award.getAwardedTo().forEach(q -> {
				quiltsToUpdate.add(q);
				q.getAwards().remove(award);
			});
		}
		
		for(Integer number : quiltNumbers) {
			Quilt q = quiltRepository.findByNumber(number).orElseThrow(() -> new NotFoundException("Quilt", number));
			quilts.add(q);
			quiltsToUpdate.add(q);
		}
		
		award.setAwardedTo(quilts);
		awardRepository.save(award);

		for(Quilt q : quilts) {
			q.getAwards().add(award);
		}

		for(Quilt q : quiltsToUpdate) {
			quiltRepository.save(q);
		}
	}
	
	public void unassignAward(Long awardId, Long quiltId) {
		Award award = getAward(awardId);
		Quilt quilt = quiltRepository.findById(quiltId).orElseThrow(() -> new NotFoundException("Quilt", quiltId));
		
		award.getAwardedTo().remove(quilt);
		quilt.getAwards().remove(award);
		
		awardRepository.save(award);
		quiltRepository.save(quilt);
	}

	
	
	// ------------------------------------------------------------------------
	// Quilt functions
	// ------------------------------------------------------------------------
	public Quilt addQuilt(Long showId, Quilt newQuilt) throws NoActiveShowException {
		Show show = (showId == null) ? getActiveShow() : getShow(showId);
		
		if(show == null) {
			throw new NoActiveShowException();
		}
		
		log.info("Adding quilt [{}] to show [{}]", newQuilt.getName(), show.getId());
		newQuilt.setShow(show);		
		Quilt created = quiltRepository.save(newQuilt);
		
		return created;
	}
	
	public void deleteQuilt(Long quiltId) {
		Optional<Quilt> quilt = quiltRepository.findById(quiltId);
		if(quilt.isPresent()) {
			deleteQuilt(quilt.get());
		}
	}
	
	public void deleteQuilt(Quilt quilt)  {
		log.info("Deleting quilt [{}]", quilt.getId());
		quilt.getAwards().forEach(a -> {
			a.getAwardedTo().remove(quilt);
			awardRepository.save(a);
		});
		
		quiltRepository.delete(quilt);
	}

}
