package org.crochet.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BannerMapper;
import org.crochet.model.Banner;
import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;
import org.crochet.repository.BannerRepo;
import org.crochet.repository.BannerTypeRepo;
import org.crochet.service.BannerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

@Slf4j
@Service
public class BannerServiceImpl implements BannerService {
    private final BannerRepo bannerRepo;
    private final BannerTypeRepo bannerTypeRepo;

    public BannerServiceImpl(BannerRepo bannerRepo,
            BannerTypeRepo bannerTypeRepo) {
        this.bannerRepo = bannerRepo;
        this.bannerTypeRepo = bannerTypeRepo;
    }

    @Transactional
    @Override
    public List<BannerResponse> batchInsertOrUpdate(List<BannerRequest> requests) {
        List<Banner> banners = new ArrayList<>();
        List<Banner> existingBanners = bannerRepo.findAll();

        for (BannerRequest request : requests) {
            Banner banner;
            if (!StringUtils.hasText(request.getId())) {
                var bannerType = bannerTypeRepo.findById(request.getBannerTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_BANNER_TYPE_NOT_FOUND,
                                MAP_CODE.get(MessageConstant.MSG_BANNER_TYPE_NOT_FOUND)));
                banner = Banner.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .url(request.getUrl())
                        .active(request.isActive())
                        .textColor(request.getTextColor())
                        .fileName(request.getFileName())
                        .fileContent(request.getFileContent())
                        .bannerType(bannerType)
                        .build();
            } else {
                banner = bannerRepo.findById(request.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_BANNER_NOT_FOUND,
                                MAP_CODE.get(MessageConstant.MSG_BANNER_NOT_FOUND)));
                banner = BannerMapper.INSTANCE.partialUpdate(request, banner);
            }
            banners.add(banner);
        }

        // Remove existing banners that are not in the new list
        existingBanners.removeAll(banners);
        bannerRepo.deleteAll(existingBanners);

        banners = bannerRepo.saveAll(banners);
        return banners.stream().map(BannerMapper.INSTANCE::toResponse).toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<BannerResponse> getAll() {
        List<Banner> banners = bannerRepo.findActiveBanners();
        return BannerMapper.INSTANCE.toResponses(banners);
    }
}
