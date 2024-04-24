package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BannerMapper;
import org.crochet.model.Banner;
import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;
import org.crochet.repository.BannerRepo;
import org.crochet.repository.BannerTypeRepo;
import org.crochet.service.BannerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepo bannerRepo;
    private final BannerTypeRepo bannerTypeRepo;

    @Transactional
    @Override
    public BannerResponse createOrUpdateBanner(BannerRequest request) {
        Banner banner;
        if (request.getId() == null) {
            var bannerType = bannerTypeRepo.findById(request.getBannerTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Banner type not found"));
            banner = Banner.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .url(request.getUrl())
                    .fileName(request.getFileName())
                    .fileContent(request.getFileContent())
                    .bannerType(bannerType)
                    .build();
        } else {
            banner = bannerRepo.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Banner not found"));
            banner = BannerMapper.INSTANCE.partialUpdate(request, banner);
        }
        banner = bannerRepo.save(banner);
        return BannerMapper.INSTANCE.toResponse(banner);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        bannerRepo.deleteById(id);
    }

    @Override
    public List<BannerResponse> getAll() {
        List<Banner> banners = bannerRepo.findAll()
                .stream()
                .filter(Banner::isActive)
                .toList();
        return BannerMapper.INSTANCE.toResponses(banners);
    }

    @Override
    public BannerResponse getById(UUID id) {
        return BannerMapper.INSTANCE.toResponse(bannerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found")));
    }

    @Override
    public List<BannerResponse> getAllByType(String bannerTypeName) {
        var bannerType = bannerTypeRepo.findByName(bannerTypeName)
                .orElseThrow(() -> new ResourceNotFoundException("Banner type not found"));
        var banners = bannerType.getBanners()
                .stream()
                .filter(Banner::isActive)
                .toList();
        return BannerMapper.INSTANCE.toResponses(banners);
    }

    @Override
    public List<BannerResponse> getAllByType(UUID bannerTypeId) {
        var bannerType = bannerTypeRepo.findById(bannerTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner type not found"));
        var banners = bannerType.getBanners()
                .stream()
                .filter(Banner::isActive)
                .toList();
        return BannerMapper.INSTANCE.toResponses(banners);
    }
}
