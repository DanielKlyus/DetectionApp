package com.example.sber_ai.service.mapper;

import com.example.sber_ai.model.entity.Category;
import com.example.sber_ai.model.entity.Project;
import com.example.sber_ai.model.request.CreateCategoryRequest;
import com.example.sber_ai.model.response.CreateCategoryResponse;
import com.ibm.icu.text.Transliterator;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    private static final Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");

    public static String transliterateAndFormat(String text) {
        String transliterated = toLatinTrans.transliterate(text);
        return transliterated.toLowerCase().replace(" ", "_");
    }

    public Category mapToEntity(CreateCategoryRequest request, String categoryUrl, Project project) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSpecies(request.getSpecies());
        category.setImg(categoryUrl);
        category.setClassType("animal");
        category.setType(transliterateAndFormat(request.getName()));
        category.setProjectId(project);
        return category;
    }

    public CreateCategoryResponse mapToResponse(Category category) {
        CreateCategoryResponse response = new CreateCategoryResponse();
        response.setName(category.getName());
        response.setSpecies(category.getSpecies());
        response.setImg(category.getImg());
        response.setClassType(category.getClassType());
        response.setType(category.getType());
        return response;
    }
}
