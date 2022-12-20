package com.bootcamp.java.credit.web.mapper;

import com.bootcamp.java.credit.domain.Card;
import com.bootcamp.java.credit.web.model.CardModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card modelToEntity(CardModel model);
    CardModel entityToModel(Card event);
    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Card entity, Card updateEntity);
}
