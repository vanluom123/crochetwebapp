package org.crochet.service.impl;

import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BannerTypeMapper;
import org.crochet.model.BannerType;
import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;
import org.crochet.repository.BannerTypeRepo;
import org.crochet.service.BannerTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

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
        if (!StringUtils.hasText(request.getId())) {
            bannerType = new BannerType();
            bannerType.setName(request.getName());
        } else {
            bannerType = bannerTypeRepo.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_BANNER_TYPE_NOT_FOUND));
            bannerType.setName(request.getName());
        }
        bannerType = bannerTypeRepo.save(bannerType);
        return BannerTypeMapper.INSTANCE.toResponse(bannerType);
    }

    @Transactional
    @Override
    public void delete(String id) {
        bannerTypeRepo.deleteById(id);
    }

    @Override
    public List<BannerTypeResponse> getAll() {
        return BannerTypeMapper.INSTANCE.toResponses(bannerTypeRepo.findAll());
    }

    @Override
    public BannerTypeResponse getById(String id) {
        var banner = bannerTypeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(MessageConstant.MSG_BANNER_TYPE_NOT_FOUND));
        return BannerTypeMapper.INSTANCE.toResponse(banner);
    }
}
