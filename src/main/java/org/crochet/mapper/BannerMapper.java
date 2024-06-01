package org.crochet.mapper;

import org.crochet.model.Banner;
import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BannerTypeMapper.class}
)
public interface BannerMapper extends AbstractMapper<Banner, BannerResponse>, PartialUpdate<Banner, BannerRequest> {
    BannerMapper INSTANCE = Mappers.getMapper(BannerMapper.class);
}