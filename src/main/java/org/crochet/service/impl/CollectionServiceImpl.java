package org.crochet.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Collection;
import org.crochet.model.SavingChart;
import org.crochet.model.User;
import org.crochet.payload.request.CollectionRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.repository.CollectionRepository;
import org.crochet.repository.SavingChartRepo;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CollectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

@Slf4j
@Service
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final SavingChartRepo savingChartRepo;

    public CollectionServiceImpl(CollectionRepository collectionRepository,
            UserRepository userRepository,
            SavingChartRepo savingChartRepo) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.savingChartRepo = savingChartRepo;
    }

    @Override
    @Transactional
    public CollectionResponse saveCollection(CollectionRequest request, UserPrincipal principal) {
        if (principal == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_LOGIN_REQUIRED,
                    MAP_CODE.get(MessageConstant.MSG_USER_LOGIN_REQUIRED));
        }

        User user = userRepository.findById(principal.getId()).get();

        Collection collection;
        if (request.getId() == null) {
            collection = new Collection();
            if (savingChartRepo.existsSavingChartByCollectionNameContains(request.getSavingChartIds(), request.getName())) {
                log.warn("Chart added to collection");
                return new CollectionResponse();
            }
        } else {
            collection = collectionRepository.findById(request.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Collection not found"));
        }
        collection.setName(request.getName());
        collection.setUser(user);
        collection = collectionRepository.save(collection);
        var savingCharts = savingChartRepo.findAllById(request.getSavingChartIds());
        for (SavingChart savingChart : savingCharts) {
            savingChart.setCollection(collection);
        }
        savingChartRepo.saveAll(savingCharts);

        return mapToCollectionResponse(collection);
    }

    @Override
    public List<CollectionResponse> getAllCollections() {
        List<Collection> collections = collectionRepository.findAllWithUserAndSavingCharts();
        return collections.stream()
                .map(this::mapToCollectionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollectionResponse> getAllCollectionsByUserId(String userId) {
        List<Collection> collections = collectionRepository.findAllByUserIdWithSavingCharts(userId);
        return collections.stream()
                .map(this::mapToCollectionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CollectionResponse getCollectionById(String id) {
        return mapToCollectionResponse(findCollectionById(id));
    }

    @Override
    @Transactional
    public void deleteCollection(String id) {
        Collection collection = findCollectionById(id);
        collectionRepository.delete(collection);
    }

    private Collection findCollectionById(String id) {
        return collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with id: " + id));
    }

    private CollectionResponse mapToCollectionResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .userId(collection.getUser().getId())
                .build();
    }
}
