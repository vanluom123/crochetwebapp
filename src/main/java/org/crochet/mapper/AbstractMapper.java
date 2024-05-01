package org.crochet.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

public interface AbstractMapper<Entity, Response> {
    Response toResponse(Entity entity);

    Entity toEntity(Response response);

    List<Response> toResponses(Collection<Entity> entities);

    List<Entity> toEntities(Collection<Response> responses);
}

interface PartialUpdate<Target, Source> {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Target partialUpdate(Source source, @MappingTarget Target target);
}
