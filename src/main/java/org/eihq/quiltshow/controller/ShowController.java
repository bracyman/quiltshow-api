package org.eihq.quiltshow.controller;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eihq.quiltshow.configuration.UserRoles;
import org.eihq.quiltshow.controller.models.ShowSummary;
import org.eihq.quiltshow.exception.CannotDeleteInUseException;
import org.eihq.quiltshow.exception.NoActiveShowException;
import org.eihq.quiltshow.exception.NotFoundException;
import org.eihq.quiltshow.model.Award;
import org.eihq.quiltshow.model.Category;
import org.eihq.quiltshow.model.GroupSize;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Show;
import org.eihq.quiltshow.model.Tag;
import org.eihq.quiltshow.model.TagCategory;
import org.eihq.quiltshow.service.ShowService;
import org.eihq.quiltshow.service.UserAuthentication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/shows")
public class ShowController implements InitializingBean {

	@Autowired
	ShowService showService;

	@Autowired
	UserAuthentication userAuthentication;

	@GetMapping("/")
	public ResponseEntity<List<ShowSummary>> getShows() {
		return ResponseEntity.ok(showService.getAllShows().stream().map(s -> ShowSummary.from(s)).toList());
	}

	@GetMapping("/{showId}")
	public ResponseEntity<Show> getShow(@PathVariable("showId")String showId) {
		if(showId.equalsIgnoreCase("active")) {
			return ResponseEntity.ok(showService.getActiveShow());
		}
		if(showId.equalsIgnoreCase("current")) {
			return ResponseEntity.ok(showService.getCurrentShow());
		}

		Long longId = Long.parseLong(showId);
		return ResponseEntity.ok(showService.getShow(longId));
	}

	@PostMapping("/")
	public ResponseEntity<Show> createShow(Show newShow) {
		Show created = showService.createShow(newShow);
		return ResponseEntity.ok(created);
	}

	@PostMapping("/{showId}")
	public ResponseEntity<Show> updateShow(@PathVariable("showId")Long showId, Show updatedShow) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Show show = showService.updateShow(updatedShow);
		return ResponseEntity.ok(show);
	}

	@DeleteMapping("/{showId}")
	public ResponseEntity<Long> deleteShow(@PathVariable("showId")Long showId) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		showService.deleteShow(showId);
		return ResponseEntity.ok(showId);
	}


	/**
	 * Returns all the categories for the currently active, or most recently active show
	 * @return
	 * @throws NoSuchShowException
	 */
	@GetMapping("/categories")
	public ResponseEntity<Set<Category>> getCategories() {
		Show currentShow = showService.getCurrentShow();
		if(currentShow != null) {
			return ResponseEntity.ok(showService.getCurrentShow().getCategories());
		}

		throw new NotFoundException("No shows can be found");
	}

	/**
	 * Returns all the categories for the specified show
	 * @param showId
	 * @return
	 * @throws NoSuchShowException
	 */
	@GetMapping("/{showId}/categories")
	public ResponseEntity<Set<Category>> getCategories(@PathVariable("showId")Long showId) {
		Show currentShow = showService.getCurrentShow();
		if(currentShow == null) {
			throw new NotFoundException("No shows can be found");
		}
		
		return ResponseEntity.ok(showService.getShow(showId).getCategories());
	}

	@PostMapping("/{showId}/categories")
	public ResponseEntity<Category> createCategory(@PathVariable("showId")Long showId, Category newCategory) throws NoActiveShowException {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Category created = showService.createCategory(showId, newCategory);
		return ResponseEntity.ok(created);
	}

	@PostMapping("/{showId}/categories/{categoryId}")
	public ResponseEntity<Category> updateCategory(@PathVariable("showId")Long showId, @PathVariable("categoryId") Long categoryId, Category category) {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Category updated = showService.updateCategory(category);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{showId}/categories/{categoryId}")
	public ResponseEntity<Long> deleteCategory(@PathVariable("showId")Long showId, @PathVariable("categoryId") Long categoryId) throws CannotDeleteInUseException {
		if(!userAuthentication.hasRole(UserRoles.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		showService.deleteCategory(categoryId);
		return ResponseEntity.ok(categoryId);
	}
	
	
	@GetMapping("/{showId}/tagCategories")
	public ResponseEntity<Set<TagCategory>> getTagCategories(@PathVariable("showId")Long showId) {
		return ResponseEntity.ok(showService.getShow(showId).getTagCategories());
	}
	
	@GetMapping("/{showId}/tagCategories/{tagCategoryId}")
	public ResponseEntity<TagCategory> getTagCategory(@PathVariable("showId") Long showId, @PathVariable("tagCategoryId") Long tagCategoryId) {
		Optional<TagCategory> tagCategory = showService.getShow(showId).getTagCategories().stream().filter(tc -> tc.getId().equals(tagCategoryId)).findFirst();
		
		if(tagCategory.isEmpty()) {
			throw new NotFoundException("TagCategory", tagCategoryId);
		}
		
		return ResponseEntity.ok(tagCategory.get());
	}
	
	@PostMapping("/{showId}/tagCategories")
	public ResponseEntity<TagCategory> createTagCategory(@PathVariable("showId") Long showId, TagCategory tagCategory) throws NoActiveShowException {
		TagCategory newTagCategory = showService.createTagCategory(showId, tagCategory);
		
		return ResponseEntity.ok(newTagCategory);
	}
	
	@PostMapping("/{showId}/tagCategories/{tagCategoryId}")
	public ResponseEntity<TagCategory> updateTagCategory(@PathVariable("showId") Long showId, @PathVariable("tagCategoryId") Long tagCategoryId, TagCategory tagCategory) throws NoActiveShowException {
		TagCategory updatedTagCategory = showService.updateTagCategory(tagCategoryId, tagCategory);
		
		return ResponseEntity.ok(updatedTagCategory);
	}
	
	@DeleteMapping("/{showId}/tagCategories/{tagCategoryId}")
	public ResponseEntity<Long> deleteTagCategory(@PathVariable("showId") Long showId, @PathVariable("tagCategoryId") Long tagCategoryId) throws NoActiveShowException {
		showService.deleteTagCategory(showId, tagCategoryId);
		return ResponseEntity.ok(tagCategoryId);
	}



	/**
	 * Returns all the tags for the currently active, or most recently active show
	 * @return
	 * @throws NoSuchShowException
	 */
	@GetMapping("/tagCategories")
	public ResponseEntity<Set<TagCategory>> getTagCategories() {
		Show currentShow = showService.getCurrentShow();
		if(currentShow != null) {
			return ResponseEntity.ok(showService.getCurrentShow().getTagCategories());
		}

		throw new NotFoundException("No shows can be found");
	}	

	
	/**
	 * Returns all the Tags for the specified show
	 * @param showId
	 * @return
	 * @throws NoSuchShowException
	 */
	@GetMapping("/{showId}/tags")
	public ResponseEntity<Set<Tag>> getAllTags(@PathVariable("showId")Long showId) {
		Set<TagCategory> tagCategories = showService.getShow(showId).getTagCategories();
		Set<Tag> tags = new HashSet<>();
		
		tagCategories.forEach(tc -> tags.addAll(tc.getTags()));
		
		return ResponseEntity.ok(tags);		
	}

	/**
	 * Returns all the Tags for the specified tag category
	 * @param showId
	 * @return
	 * @throws NoSuchShowException
	 */
	@GetMapping("/{showId}/tagCategories/{tagCategoryId}/tags")
	public ResponseEntity<Set<Tag>> getCategoryTags(@PathVariable("showId")Long showId, @PathVariable("tagCategoryId")Long tagCategoryId) {
		TagCategory tagCategory = showService.getTagCategory(tagCategoryId);
		
		return ResponseEntity.ok(tagCategory.getTags());
	}

	@PostMapping("/{showId}/tagCategories/{tagCategoryId}/tags")
	public ResponseEntity<Tag> createTag(@PathVariable("showId")Long showId, @PathVariable("tagCategoryId")Long tagCategoryId, Tag newTag) throws NoActiveShowException {
		Tag created = showService.createTag(showId, newTag);
		return ResponseEntity.ok(created);
	}

	@PostMapping("/{showId}/tagCategories/{tagCategoryId}/tags/{tagId}")
	public ResponseEntity<Tag> updateTag(@PathVariable("showId")Long showId, @PathVariable("tagId") Long tagId, Tag tag) {
		Tag updated = showService.updateTag(tag);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{showId}/tagCategories/{tagCategoryId}/tags/{tagId}")
	public ResponseEntity<Long> deleteTag(@PathVariable("showId")Long showId, @PathVariable("tagId") Long tagId) {
		showService.deleteTag(tagId);
		
		return ResponseEntity.ok(tagId);
	}



	/**
	 * Returns all the awards for the currently active, or most recently active show
	 * @return
	 * @throws NoSuchShowException
	 */
	@GetMapping("/awards")
	public Set<Award> getAwards() {
		Show currentShow = showService.getCurrentShow();
		if(currentShow != null) {
			return showService.getCurrentShow().getAwards();
		}

		throw new NotFoundException("No shows can be found");
	}

	/**
	 * Returns all the awards for the specified show
	 * @param showId
	 * @return
	 * @throws NoSuchShowException
	 */
	@GetMapping("/{showId}/awards")
	public Set<Award> getAwards(@PathVariable("showId")Long showId) {
		return showService.getShow(showId).getAwards();
	}

	@PostMapping("/{showId}/awards")
	public ResponseEntity<Award> createAward(@PathVariable("showId")Long showId, Award newAward) throws NoActiveShowException {
		Award created = showService.createAward(showId, newAward);
		return ResponseEntity.ok(created);
	}

	@PostMapping("/awards/{awardId}")
	public ResponseEntity<Award> updateAward(@PathVariable("showId")Long showId, @PathVariable("awardId") Long awardId, Award award) {
		Award updated = showService.updateAward(award);
		return ResponseEntity.ok(updated);
	}

	@PostMapping("/awards/{awardId}/award/{quiltId}")
	public void assignAward(@PathVariable("awardId") Long awardId, @PathVariable("quiltId") Long quiltId) {
		showService.assignAward(awardId, quiltId);
	}

	@DeleteMapping("/awards/{awardId}/award/{quiltId}")
	public void unassignAward(@PathVariable("awardId") Long awardId, @PathVariable("quiltId") Long quiltId) {
		showService.unassignAward(awardId, quiltId);
	}

	@DeleteMapping("/{showId}/awards/{awardId}")
	public void deleteAward(@PathVariable("showId")Long showId, @PathVariable("awardId") Long awardId) throws CannotDeleteInUseException {
		showService.deleteAward(awardId);
	}



	@Override
	public void afterPropertiesSet() throws Exception {
		createDefaultShow();

		createDefaultCategories();
		
		createDefaultTagCategories();
		
		createDefaultTags();

		createDefaultAwards();
		
		createDefaultQuilt();
	}

	private void createDefaultShow() {
		Show show = new Show();
		show.setName("Test Show");
		show.setActive(true);
		show.setAddress1("123 Main St");
		show.setCity("Cedar Rapids");
		show.setState("TX");
		show.setZipCode("52401");
		show.setDescription("Just a test of the system");

		Calendar startDate = Calendar.getInstance();
		startDate.set(2023, Calendar.JULY, 28, 9, 0, 0);
		show.setStartDate(startDate.getTime());

		showService.createShow(show);
	}

	private void createDefaultCategories() {
		try {
			Long showId = showService.getActiveShow().getId();
			Category c = new Category();

			c.setName("Applique");c.setDescription("A quilt with applique as primary technique.  Applique may be sewn down with any method, but not may be only fused on.  Any size.");
			c.setShortDescription("A quilt with applique as primary technique");
			showService.createCategory(showId, c);

			c.setName("Art/Innovative Quilt");c.setDescription("A work of original design reflecting innovative construction and design techniques, themes, or subject matter. Category includes:\n"
					+ "<ul>"
					+ "<li>Abstract composition - a non-identifiable subject that stresses mostly color, line, and shape.</li>"
					+ "<li>Naturescape - a quilt that reflects the scenery found in nature, such as land, mountains, forests, gardens, and deserts.  Animals and humans may be included but should not be the main focus of the piece.</li>"
					+ "<li>Pictorial - a quilt that represents a recognizable image of a person, place, or thing.</li></ul>");
			c.setShortDescription("A work reflecting innovative techniques");
			showService.createCategory(showId, c);

			c.setName("Kits including Block of the Month and Row by Row");c.setDescription("This category includes all quilts made from any combination of units (patterns, fabrics, etc.) that were pre-selected by someone other than the quiltmaker.  Any technique, any size.");
			c.setShortDescription("A quilt made from a kit");
			showService.createCategory(showId, c);

			c.setName("Juniors");c.setDescription("Any quilter who is under age 18 as of July 21, 2023.  The Junior Quilter must have constructed the quilt themselves with only direction from an adult.  Quilting may have been completed by someone other than the Junior Quilter.");
			c.setShortDescription("Any quilter who is under age 18");
			showService.createCategory(showId, c);

			c.setName("Clothing/ Accessories");c.setDescription("Bags, clothing, pillows, dolls, mug rugs, candle mats, etc.");
			c.setShortDescription("Bags, clothing, pillows, dolls, mug rugs, etc");
			showService.createCategory(showId, c);

			c.setName("Modern");c.setDescription("A modern quilt is defined as one that includes any of the following: minimalism, asymmetry, expansive negative space, bold colors, high contrast, improvisational piecing, grid or straight line quilting.  Any size.");
			c.setShortDescription("A quilt using modern design techniques");
			showService.createCategory(showId, c);

			c.setName("Scrap Quilts");c.setDescription("Quilt top must incorporate at least 50 different fabrics.  Any technique, any size.");
			c.setShortDescription("Top incorporates at least 50 different fabrics");
			showService.createCategory(showId, c);

			c.setName("Seasonal Quilts");c.setDescription("Any quilt reflecting traditional holiday or seasonal motifs.  Examples include Christmas, Halloween, fall, winter, spring or summer themes, Easter, 4th of July, Valentine’s Day, etc.");
			c.setShortDescription("Any quilt reflecting holiday or seasonal motifs");
			showService.createCategory(showId, c);

			c.setName("Special Techniques");c.setDescription("Quilts showcasing specialized techniques such as embroidery, English paper piecing, foundation piecing, paper piecing, cathedral windows, miniature quilts (a scaled down version of a full-sized pattern), whole cloth, yo-yo quilts, heavily embellished quilts and quilts using unusual quilting techniques such as sashiko or boro.  Items do not have to have a traditional quilting stitch through three layers.");
			c.setShortDescription("Quilts showcasing specialized techniques");
			showService.createCategory(showId, c);

			c.setName("Traditional Quilt Large");c.setDescription("Total perimeter greater than 260 inches.");
			c.setShortDescription("Total perimeter greater than 260 inches.");
			showService.createCategory(showId, c);

			c.setName("Traditional Quilt Small");c.setDescription("Total perimeter less than 260 inches.");
			c.setShortDescription("Total perimeter less than 260 inches");
			showService.createCategory(showId, c);
		}
		catch(NoActiveShowException e) {

		}
	}

	void createDefaultTagCategories() throws NoActiveShowException {
		Long showId = showService.getActiveShow().getId();
		TagCategory tagCategory = new TagCategory();
		
		tagCategory.setName("Quilting Style");tagCategory.setDescription("Quilting approached used");tagCategory.setRequireOne(true);tagCategory.setOnlyOne(true);
		tagCategory = showService.createTagCategory(showId, tagCategory);
		
		tagCategory = new TagCategory();
		tagCategory.setName("Embroidery");tagCategory.setDescription("Embroidery technique");tagCategory.setRequireOne(false);
		tagCategory = showService.createTagCategory(showId, tagCategory);

		tagCategory = new TagCategory();
		tagCategory.setName("Piecing Type");tagCategory.setDescription("How the quilt was pieced");tagCategory.setRequireOne(true);tagCategory.setOnlyOne(true);
		tagCategory = showService.createTagCategory(showId, tagCategory);
		
		tagCategory = new TagCategory();
		tagCategory.setName("Miscellaneous");tagCategory.setDescription("Other features of the quilt");tagCategory.setRequireOne(false);
		tagCategory = showService.createTagCategory(showId, tagCategory);

		tagCategory = new TagCategory();
		tagCategory.setName("Seasonal");tagCategory.setDescription("Season quilt descriptions");tagCategory.setRequireOne(false);
		tagCategory = showService.createTagCategory(showId, tagCategory);
	}
	
	void createDefaultTags() {
		try {
			// create some default tags
			Show show = showService.getActiveShow();
			TagCategory tagCategory = new TagCategory();
			Tag t = new Tag();
			

			// ----------------------
			tagCategory = show.getTagCategories().stream().filter(tc -> tc.getName().equals("Quilting Style")).findFirst().get();
			t.setName("Hand Quilted");showService.createTag(tagCategory, t);
			t.setName("Station Machine Quilted");showService.createTag(tagCategory, t);
			t.setName("Hand Guided Machine Quilted");showService.createTag(tagCategory, t);
			t.setName("Computer Guided Machine Quilted");showService.createTag(tagCategory, t);
			t.setName("Trapunto");showService.createTag(tagCategory, t);
			t.setName("Wholecloth");showService.createTag(tagCategory, t);

			// ----------------------
			tagCategory = show.getTagCategories().stream().filter(tc -> tc.getName().equals("Embroidery")).findFirst().get();
			t.setName("Hand Embroidered");showService.createTag(tagCategory, t);
			t.setName("Machine Embroidered");showService.createTag(tagCategory, t);

			// ----------------------
			tagCategory = show.getTagCategories().stream().filter(tc -> tc.getName().equals("Piecing Type")).findFirst().get();
			t.setName("English Paper Piecing");showService.createTag(tagCategory, t);
			t.setName("Foundation Piecing");showService.createTag(tagCategory, t);
			t.setName("Paper Piecing");showService.createTag(tagCategory, t);
			t.setName("Hand Piecing");showService.createTag(tagCategory, t);
			t.setName("Machine Piecing");showService.createTag(tagCategory, t);
			
			// ----------------------
			tagCategory = show.getTagCategories().stream().filter(tc -> tc.getName().equals("Miscellaneous")).findFirst().get();
			t.setName("Panel");showService.createTag(tagCategory, t);			
			t.setName("Wool");showService.createTag(tagCategory, t);
			t.setName("Miniature");showService.createTag(tagCategory, t);
			t.setName("Cathedral Window");showService.createTag(tagCategory, t);
			t.setName("Sashiko");showService.createTag(tagCategory, t);
			t.setName("Yoyo");showService.createTag(tagCategory, t);
			

			// ----------------------
			tagCategory = show.getTagCategories().stream().filter(tc -> tc.getName().equals("Seasonal")).findFirst().get();
			t.setName("Winter");showService.createTag(tagCategory, t);
			t.setName("Christmas");showService.createTag(tagCategory, t);
			t.setName("Spring");showService.createTag(tagCategory, t);
			t.setName("Easter");showService.createTag(tagCategory, t);
			t.setName("Fall");showService.createTag(tagCategory, t);
			t.setName("Halloween");showService.createTag(tagCategory, t);
			t.setName("Valentine's");showService.createTag(tagCategory, t);
			t.setName("Summer");showService.createTag(tagCategory, t);
			t.setName("4th of July");showService.createTag(tagCategory, t);
			t.setName("Thanksgiving");showService.createTag(tagCategory, t);
		}
		catch(NotFoundException e) {

		}
	}

	private void createDefaultAwards() {
		// TODO Auto-generated method stub

	}

	private void createDefaultQuilt() {
		Show show = showService.getActiveShow();
		
		Quilt quilt = new Quilt();
		quilt.setName("nombre");
		quilt.setDescription("description");
		quilt.setWidth(45);
		quilt.setLength(54);
		quilt.setGroupSize(GroupSize.SOLO);
		quilt.setJudged(true);
		quilt.setCategory(show.getCategories().iterator().next());
		quilt.getTags().add(show.getTagCategories().iterator().next().getTags().iterator().next());
		
		try {
			showService.addQuilt(show.getId(), quilt);
		} catch (NoActiveShowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
