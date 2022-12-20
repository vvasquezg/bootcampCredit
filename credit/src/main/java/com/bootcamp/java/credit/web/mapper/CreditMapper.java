package com.bootcamp.java.credit.web.mapper;

import com.bootcamp.java.credit.domain.Credit;
import com.bootcamp.java.credit.web.model.CreditModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit modelToEntity(CreditModel model);

    CreditModel entityToModel(Credit event);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Credit entity, Credit updateEntity);
}
