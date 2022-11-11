package org.eihq.quiltshow.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eihq.quiltshow.model.Tag;
import org.eihq.quiltshow.repository.TagRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tags")
public class TagController implements InitializingBean {
        
    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    @GetMapping("/{name}")
    public Tag getTag(@PathVariable String name) {
        return tagRepository.findById(name).get();
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) throws URISyntaxException {
        Tag newTag = tagRepository.save(tag);
        return ResponseEntity.created(new URI("/tags/%s".formatted(newTag.getName()))).body(newTag);
    }
    
    @PutMapping("/{name}")
    public ResponseEntity<Tag> updateClient(@PathVariable String name, @RequestBody Tag tag) {
        Tag currentTag = tagRepository.findById(name).orElseThrow(RuntimeException::new);
        currentTag.setName(tag.getName());
        currentTag.setDescription(tag.getDescription());
        
        currentTag = tagRepository.save(currentTag);

        return ResponseEntity.ok(currentTag);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deletTag(@PathVariable String name) {
        tagRepository.deleteById(name);
        return ResponseEntity.ok().build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // create some default tags
        Tag t = new Tag();
        t.setName("Applique");
        t.setDescription("An applique quilt, machine or hand");
        tagRepository.save(t);

        t = new Tag();
        t.setName("Hand");
        t.setDescription("Quilting done by hand");
        tagRepository.save(t);

        t = new Tag();
        t.setName("Purchased Pattern");
        t.setDescription("Quilt follows a purchased pattern");
        tagRepository.save(t);
    }
}
