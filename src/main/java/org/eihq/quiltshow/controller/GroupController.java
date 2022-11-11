package org.eihq.quiltshow.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eihq.quiltshow.model.Group;
import org.eihq.quiltshow.repository.GroupRepository;
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
@RequestMapping("/groups")
public class GroupController {
            
    @Autowired
    private GroupRepository groupRepository;

    @GetMapping
    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable Long id) {
        return groupRepository.findById(id).get();
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) throws URISyntaxException {
        Group newGroup = groupRepository.save(group);
        return ResponseEntity.created(new URI("/groups/%d".formatted(newGroup.getId()))).body(newGroup);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Group> updateClient(@PathVariable Long id, @RequestBody Group group) {
        Group currentGroup = groupRepository.findById(id).orElseThrow(RuntimeException::new);
        currentGroup.setName(group.getName());
        currentGroup.setTags(group.getTags());
        
        currentGroup = groupRepository.save(currentGroup);

        return ResponseEntity.ok(currentGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletGroup(@PathVariable Long id) {
        groupRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
