package org.crochet.mapper;

import org.crochet.model.BannerType;
import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BannerTypeMapper extends AbstractMapper<BannerType, BannerTypeResponse>,
        PartialUpdate<BannerType, BannerTypeRequest> {
    BannerTypeMapper INSTANCE = Mappers.getMapper(BannerTypeMapper.class);
}