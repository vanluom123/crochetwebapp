package org.crochet.service;

import org.crochet.exception.AccessDeniedException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.ColFrep;
import org.crochet.model.Collection;
import org.crochet.model.FreePattern;
import org.crochet.model.User;
import org.crochet.repository.ColFrepRepo;
import org.crochet.repository.CollectionRepo;
import org.crochet.repository.FreePatternRepository;
import org.crochet.service.impl.CollectionServiceImpl;
import org.crochet.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CollectionServiceTest {

    @Mock
    private CollectionRepo collectionRepo;

    @Mock
    private FreePatternRepository freePatternRepository;

    @Mock
    private ColFrepRepo colFrepRepo;

    @Mock
    private CollectionAvatarService avatarService;

    private CollectionServiceImpl collectionService;

    @BeforeEach
    void setUp() {
        collectionService = new CollectionServiceImpl(collectionRepo, freePatternRepository, colFrepRepo, avatarService);
    }

    @Test
    void testAddFreePatternToCollection_Success_FirstPattern() {
        // Arrange
        String collectionId = "col-123";
        String freePatternId = "fp-456";

        Collection collection = new Collection();
        collection.setId(collectionId);
        FreePattern freePattern = new FreePattern();
        freePattern.setId(freePatternId);

        when(collectionRepo.findColById(collectionId)).thenReturn(Optional.of(collection));
        when(freePatternRepository.findFrepById(freePatternId)).thenReturn(Optional.of(freePattern));
        // Simulate it is the first pattern in the collection
        when(colFrepRepo.countByCollectionId(collectionId)).thenReturn(1L);

        // Act
        collectionService.addFreePatternToCollection(collectionId, freePatternId);

        // Assert
        // Capture the saved ColFrep
        ArgumentCaptor<ColFrep> captor = ArgumentCaptor.forClass(ColFrep.class);
        verify(colFrepRepo, times(1)).save(captor.capture());
        ColFrep savedColFrep = captor.getValue();
        assertEquals(collection, savedColFrep.getCollection());
        assertEquals(freePattern, savedColFrep.getFreePattern());

        // Verify avatar update is triggered when first pattern is added
        verify(avatarService, times(1)).updateAvatar(eq(collection), eq(freePattern));
    }

    @Test
    void testAddFreePatternToCollection_Success_NotFirstPattern() {
        // Arrange
        String collectionId = "col-123";
        String freePatternId = "fp-456";

        Collection collection = new Collection();
        collection.setId(collectionId);
        FreePattern freePattern = new FreePattern();
        freePattern.setId(freePatternId);

        when(collectionRepo.findColById(collectionId)).thenReturn(Optional.of(collection));
        when(freePatternRepository.findFrepById(freePatternId)).thenReturn(Optional.of(freePattern));
        // Simulate collection already has one or more patterns
        when(colFrepRepo.countByCollectionId(collectionId)).thenReturn(2L);

        // Act
        collectionService.addFreePatternToCollection(collectionId, freePatternId);

        // Assert
        verify(colFrepRepo, times(1)).save(any(ColFrep.class));
        // Avatar update should not be invoked since it is not the first pattern (count != 1)
        verify(avatarService, never()).updateAvatar(any(), any());
    }

    @Test
    void testAddFreePatternToCollection_CollectionNotFound() {
        // Arrange
        String collectionId = "col-123";
        String freePatternId = "fp-456";

        when(collectionRepo.findColById(collectionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                collectionService.addFreePatternToCollection(collectionId, freePatternId));

        // Ensure freePatternRepository is never called when collection is missing
        verify(freePatternRepository, never()).findFrepById(anyString());
    }

    @Test
    void testAddFreePatternToCollection_FreePatternNotFound() {
        // Arrange
        String collectionId = "col-123";
        String freePatternId = "fp-456";

        Collection collection = new Collection();
        collection.setId(collectionId);

        when(collectionRepo.findColById(collectionId)).thenReturn(Optional.of(collection));
        when(freePatternRepository.findFrepById(freePatternId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                collectionService.addFreePatternToCollection(collectionId, freePatternId));

        // Verify that colFrepRepo.save is never called
        verify(colFrepRepo, never()).save(any(ColFrep.class));
    }

    @Test
    void testRemoveFreePatternFromCollection_Success() {
        String freePatternId = "fp-456";
        User user = new User();
        user.setId("user-123");

        FreePattern freePattern = new FreePattern();
        freePattern.setId(freePatternId);
        freePattern.setCreatedBy("user-123");

        Collection collection = new Collection();
        collection.setId("col-789");

        // Mock static call SecurityUtils.getCurrentUser()
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(user);

            when(freePatternRepository.findFrepById(freePatternId)).thenReturn(Optional.of(freePattern));
            when(colFrepRepo.findColByUserAndFreePattern(user.getId(), freePatternId)).thenReturn(collection);

            collectionService.removeFreePatternFromCollection(freePatternId);

            verify(colFrepRepo, times(1)).removeByFreePattern(freePatternId);
            verify(avatarService, times(1)).updateAvatarFromNextPattern(collection);
        }
    }

    @Test
    void testRemoveFreePatternFromCollection_UserNotFound() {
        String freePatternId = "fp-456";

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () ->
                    collectionService.removeFreePatternFromCollection(freePatternId));

            verify(freePatternRepository, never()).findFrepById(anyString());
            verify(colFrepRepo, never()).removeByFreePattern(anyString());
            verify(avatarService, never()).updateAvatarFromNextPattern(any());
        }
    }

    @Test
    void testRemoveFreePatternFromCollection_FreePatternNotFound() {
        String freePatternId = "fp-456";
        User user = new User();
        user.setId("user-123");

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(user);

            when(freePatternRepository.findFrepById(freePatternId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    collectionService.removeFreePatternFromCollection(freePatternId));

            verify(colFrepRepo, never()).removeByFreePattern(anyString());
            verify(avatarService, never()).updateAvatarFromNextPattern(any());
        }
    }

    @Test
    void testRemoveFreePatternFromCollection_AccessDeniedForFreePattern() {
        String freePatternId = "fp-456";
        User user = new User();
        user.setId("user-123");

        FreePattern freePattern = new FreePattern();
        freePattern.setId(freePatternId);
        // Set a different createdBy to simulate access denied
        freePattern.setCreatedBy("user-999");

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(user);

            when(freePatternRepository.findFrepById(freePatternId)).thenReturn(Optional.of(freePattern));

            assertThrows(AccessDeniedException.class, () ->
                    collectionService.removeFreePatternFromCollection(freePatternId));

            verify(colFrepRepo, never()).removeByFreePattern(anyString());
            verify(avatarService, never()).updateAvatarFromNextPattern(any());
        }
    }
}
