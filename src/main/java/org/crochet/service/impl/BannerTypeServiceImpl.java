package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BannerTypeMapper;
import org.crochet.model.BannerType;
import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;
import org.crochet.repository.BannerTypeRepo;
import org.crochet.service.BannerTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BannerTypeServiceImpl implements BannerTypeService {
    final BannerTypeRepo bannerTypeRepo;

    public BannerTypeServiceImpl(BannerTypeRepo bannerTypeRepo) {
        this.bannerTypeRepo = bannerTypeRepo;
    }

    @Transactional
    @Override
    public BannerTypeResponse createOrUpdate(BannerTypeRequest request) {
        BannerType bannerType;
        if (request.getId() == null) {
            bannerType = new BannerType();
            bannerType.setName(request.getName());
        } else {
            bannerType = bannerTypeRepo.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Banner type not found"));
            bannerType.setName(request.getName());
        }
        bannerType = bannerTypeRepo.save(bannerType);
        return BannerTypeMapper.INSTANCE.toResponse(bannerType);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        bannerTypeRepo.deleteById(id);
    }

    @Override
    public List<BannerTypeResponse> getAll() {
        return BannerTypeMapper.INSTANCE.toResponses(bannerTypeRepo.findAll());
    }

    @Override
    public BannerTypeResponse getById(UUID id) {
        var banner = bannerTypeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner type not found"));
        return BannerTypeMapper.INSTANCE.toResponse(banner);
    }
}
