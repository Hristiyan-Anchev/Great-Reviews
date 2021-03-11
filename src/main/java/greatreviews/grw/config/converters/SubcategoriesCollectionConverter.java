package greatreviews.grw.config.converters;

import greatreviews.grw.entities.SubcategoryEntity;
import org.modelmapper.AbstractConverter;

import java.util.Set;
import java.util.stream.Collectors;

public class SubcategoriesCollectionConverter extends AbstractConverter<Set<SubcategoryEntity>, Set<String>> {
    @Override
    protected Set<String> convert(Set<SubcategoryEntity> subcategories) {

        return subcategories
                .stream()
                .map(SubcategoryEntity::getName)
                .collect(Collectors.toSet());
    }
}
