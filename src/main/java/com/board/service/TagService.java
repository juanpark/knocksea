package com.board.service;

import com.board.domain.Tag;
import com.board.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepo;

    public TagService(TagRepository tagRepo) {
        this.tagRepo = tagRepo;
    }

    public List<Tag> findAll() {
        return tagRepo.findAll();
    }

    public Tag create(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepo.save(tag);
    }

    public void delete(Long id) {
        tagRepo.deleteById(id);
    }
}
