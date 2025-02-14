package org.crochet.service.impl;

import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BannerTypeMapper;
import org.crochet.model.BannerType;
import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;
import org.crochet.repository.BannerTypeRepo;
import org.crochet.service.BannerTypeService;
import org.crochet.util.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BannerTypeServiceImpl implements BannerTypeService {
    final BannerTypeRepo bannerTypeRepo;

    /**
     * Constructor
     *
     * @param bannerTypeRepo BannerTypeRepo
     */
    public BannerTypeServiceImpl(BannerTypeRepo bannerTypeRepo) {
        this.bannerTypeRepo = bannerTypeRepo;
    }

    /**
     * Create or update banner type
     *
     * @param request BannerTypeRequest
     * @return BannerTypeResponse
     */
    @Transactional
    @Override
    public BannerTypeResponse createOrUpdate(BannerTypeRequest request) {
        BannerType bannerType;
        if (!ObjectUtils.hasText(request.getId())) {
            bannerType = new BannerType();
            bannerType.setName(request.getName());
        } else {
            bannerType = bannerTypeRepo.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ResultCode.MSG_BANNER_TYPE_NOT_FOUND.message(),
                            ResultCode.MSG_BANNER_TYPE_NOT_FOUND.code()
                    ));
            bannerType.setName(request.getName());
        }
        bannerType = bannerTypeRepo.save(bannerType);
        return BannerTypeMapper.INSTANCE.toResponse(bannerType);
    }

    /**
     * Delete banner type
     *
     * @param id String
     */
    @Transactional
    @Override
    public void delete(String id) {
        bannerTypeRepo.deleteById(id);
    }

    /**
     * Get all banner types
     *
     * @return List of BannerTypeResponse
     */
    @Override
    public List<BannerTypeResponse> getAll() {
        return BannerTypeMapper.INSTANCE.toResponses(bannerTypeRepo.findAll());
    }

    /**
     * Get banner type by id
     *
     * @param id String
     * @return BannerTypeResponse
     */
    @Override
    public BannerTypeResponse getById(String id) {
        var banner = bannerTypeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_BANNER_TYPE_NOT_FOUND.message(),
                        ResultCode.MSG_BANNER_TYPE_NOT_FOUND.code()
                ));
        return BannerTypeMapper.INSTANCE.toResponse(banner);
    }
}
