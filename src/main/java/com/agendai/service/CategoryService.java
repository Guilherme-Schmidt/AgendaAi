package com.agendai.service;

import com.agendai.dto.CategoryDTO;
import com.agendai.model.Category;
import com.agendai.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Categoria com este nome já existe");
        }

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllOrderByName();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));

        if (dto.getName() != null && !dto.getName().equals(category.getName()) &&
                categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Categoria com este nome já existe");
        }

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada com ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}

