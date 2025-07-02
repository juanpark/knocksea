package com.board.controller;

import com.board.domain.Tag;
import com.board.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<Tag> getAll() {
        return tagService.findAll();
    }

    @PostMapping
    public ResponseEntity<Tag> create(@RequestParam String name) {
        return ResponseEntity.ok(tagService.create(name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
