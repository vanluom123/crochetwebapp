package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Collection;
import org.crochet.model.SavingChart;
import org.crochet.payload.request.CollectionRequest;
import org.crochet.payload.response.CollectionResponse;
import org.crochet.repository.CollectionRepository;
import org.crochet.repository.SavingChartRepo;
import org.crochet.repository.UserRepository;
import org.crochet.service.CollectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public CollectionResponse saveCollection(CollectionRequest request) {
        Collection collection = request.getId() != null ? findCollectionById(request.getId()) : new Collection();
        collection.setName(request.getName());
        collection.setUser(userRepository.getReferenceById(request.getUserId()));
        collection.setSavingCharts(savingChartRepo.findAllById(request.getSavingChartIds()));
        
        Collection savedCollection = collectionRepository.save(collection);
        return mapToCollectionResponse(savedCollection);
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
                .savingChartIds(collection.getSavingCharts().stream()
                        .map(SavingChart::getId)
                        .collect(Collectors.toList()))
                .build();
    }
}
