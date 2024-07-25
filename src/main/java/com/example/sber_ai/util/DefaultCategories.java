package com.example.sber_ai.util;

import com.example.sber_ai.model.entity.Category;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class DefaultCategories {
    private final List<Category> categories = new ArrayList<>(List.of(
            new Category("Бурый медведь", "UA", "animal", "brown_bear"),
            new Category("Кабан", "SS", "animal", "wild_boar"),
            new Category("Рысь", "LL", "animal", "lynx"),
            new Category("Пятнистый олень", "CN", "animal", "sika_deer"),
            new Category("Азиатский барсук", "ML", "animal", "asian_badger"),
            new Category("Дальневосточный лесной кот", "PE", "animal", "amur_forest_cat"),
            new Category("Гималайский медведь", "UT", "animal", "asian_black_bear"),
            new Category("Соболь", "MZ", "animal", "sable"),
            new Category("Енотовидная собака", "NP", "animal", "common_raccoon_dog"),
            new Category("Изюбрь", "CE", "animal", "manchurian_wapiti"),
            new Category("Сибирская косуля", "CP", "animal", "siberian_roe_deer"),
            new Category("Тигр", "PT", "animal", "tiger"),
            new Category("Человек", "HUMEN", "human", "person"),
            new Category("Транспорт", "TRAN", "vehicle", "vehicle"),
            new Category("Неопределенный вид", "OTHERS", "unknown", "unknown"),
            new Category("Пусто", "EMPTY", "empty", "empty"),
            new Category("Все животные", "ANIMALS", "animal", "animal")
    ));
}