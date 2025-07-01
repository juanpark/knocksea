package com.board.service;

import com.board.domain.Category;
import com.board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    public Category create(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepo.save(category);
    }

    public void delete(Long id) {
        categoryRepo.deleteById(id);
    }
}
