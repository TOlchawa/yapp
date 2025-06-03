package com.memoritta.server.controller;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.client.UserRepository;
import com.memoritta.server.config.ServerConfig;
import com.memoritta.server.dao.ItemDao;
import com.memoritta.server.manager.BinaryDataManager;
import com.memoritta.server.manager.ItemManager;
import com.memoritta.server.mapper.ItemMapper;
import com.memoritta.server.model.Item;
import com.memoritta.server.model.SearchSimilarRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {ItemControllerTest.Config.class, ItemManager.class, ItemController.class, UserRepository.class})
// TODO: remove UserRepository - use JWT with userId
class ItemControllerTest {

    @Configuration
    public static class Config {

        @Bean
        UserRepository getUserRepository() {
            return mock(UserRepository.class); // TODO remove it - instead use userId from JWT
        }

        @Bean
        ItemRepository getItemRepository() {
            return mock(ItemRepository.class);
        }

        @Bean
        RedisTemplate<String, String> getRedisTemplate() {
            return mock(RedisTemplate.class);
        }

        @Bean
        BinaryDataManager getBinaryDataManager() {
            return mock(BinaryDataManager.class);
        }
    }

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemManager itemManager;

    @Autowired
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        reset(itemRepository);
    }

    @Test
    void testCreateItem_whenAllFieldsProvided_shouldReturnUuid() throws IOException {
        // Given
        String name = "Test Item";
        String note = "This is a note";
        String barcode = "1234567890";
        byte[] imageBytes = "fake-image-data".getBytes();
        MockMultipartFile picture = new MockMultipartFile("picture", "file.jpg", "image/jpeg", imageBytes);

        UUID generatedId = UUID.randomUUID();
        when(itemRepository.save(any(ItemDao.class))).thenReturn(ItemDao.builder().id(generatedId).build());

        // When
        UUID result = itemController.createItem(name, note, barcode, picture);

        // Then
        assertNotNull(result);
        UUID parsedUuid = UUID.fromString(result.toString());
        assertThat(parsedUuid).isNotNull();

        verify(itemRepository).save(any(ItemDao.class));
    }

    @Test
    void testCreateItemWithImage_whenNoOptionalFields_shouldStillSaveItem() throws IOException {
        // Given
        String name = "Item Only Name";
        UUID generatedId = UUID.randomUUID();

        when(itemRepository.save(any(ItemDao.class))).thenReturn(ItemDao.builder().id(generatedId).build());

        // When
        UUID result = itemController.createItem(name, null, null, null);

        // Then
        assertNotNull(result);
        UUID parsedUuid = UUID.fromString(result.toString());
        assertThat(parsedUuid).isNotNull();

        verify(itemRepository).save(any(ItemDao.class));
    }

    @Test
    void testSearchSimilarItems_shouldReturnSingleItem() {
        // Given
        UUID id = UUID.randomUUID();
        when(itemRepository.findById(id)).thenReturn(java.util.Optional.of(ItemDao.builder().id(id).build()));

        SearchSimilarRequest request = SearchSimilarRequest.builder()
                .id(id.toString())
                .build();

        // When
        List<Item> result = itemController.searchSimilarItems(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(id, result.get(0).getId());

        verify(itemRepository).findById(id);
    }

}
