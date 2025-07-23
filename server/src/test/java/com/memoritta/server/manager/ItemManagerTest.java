package com.memoritta.server.manager;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.client.UserRepository;
import com.memoritta.server.dao.ItemDao;
import com.memoritta.server.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {ItemManagerTest.Config.class, ItemManager.class})
class ItemManagerTest {

    @Configuration
    static class Config {
        @Bean
        UserRepository getUserRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        ItemRepository getItemRepository() {
            return mock(ItemRepository.class);
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

    @BeforeEach
    void resetMocks() {
        reset(itemRepository);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("searchScenarios")
    void searchItemsByTags_parameterized(String description,
                                         List<ItemDao> repositoryItems,
                                         List<String> tags,
                                         boolean matchAll,
                                         List<UUID> expectedIds) {
        when(itemRepository.findAll()).thenReturn(repositoryItems);

        List<Item> result = itemManager.searchItemsByTags(tags, matchAll);
        List<UUID> resultIds = result.stream().map(Item::getId).toList();

        assertThat(resultIds).containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> searchScenarios() {
        UUID id1 = UUID.randomUUID();
        ItemDao beer = ItemDao.builder().id(id1).tags(Map.of("category", "beer")).build();
        ItemDao wine = ItemDao.builder().id(UUID.randomUUID()).tags(Map.of("category", "wine")).build();

        UUID id2_1 = UUID.randomUUID();
        ItemDao kvBeer = ItemDao.builder().id(id2_1).tags(Map.of("category", "beer")).build();
        UUID id2_2 = UUID.randomUUID();
        ItemDao kvOrigin = ItemDao.builder().id(id2_2).tags(Map.of("origin", "pl")).build();
        UUID id2_3 = UUID.randomUUID();
        ItemDao kvBoth = ItemDao.builder().id(id2_3).tags(Map.of("category", "beer", "origin", "pl")).build();

        UUID id3 = UUID.randomUUID();
        ItemDao tagBeer = ItemDao.builder().id(id3).tags(Map.of("beer", "")).build();
        ItemDao tagOther = ItemDao.builder().id(UUID.randomUUID()).tags(Map.of("category", "beer")).build();

        UUID id4_1 = UUID.randomUUID();
        ItemDao tagBeer2 = ItemDao.builder().id(id4_1).tags(Map.of("beer", "")).build();
        UUID id4_2 = UUID.randomUUID();
        ItemDao tagWine = ItemDao.builder().id(id4_2).tags(Map.of("wine", "")).build();
        ItemDao tagNo = ItemDao.builder().id(UUID.randomUUID()).tags(Map.of("category", "beer")).build();

        UUID id5 = UUID.randomUUID();
        ItemDao both = ItemDao.builder().id(id5).tags(Map.of("category", "beer", "beer", "")).build();
        UUID id5_1 = UUID.randomUUID();
        ItemDao onlyCategory = ItemDao.builder().id(id5_1).tags(Map.of("category", "beer")).build();
        UUID id5_2 = UUID.randomUUID();
        ItemDao onlyTag = ItemDao.builder().id(id5_2).tags(Map.of("beer", "")).build();

        return java.util.stream.Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        "key-value matchAll true",
                        List.of(beer, wine),
                        List.of("category=beer"),
                        true,
                        List.of(id1)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "key-value matchAll false",
                        List.of(kvBeer, kvOrigin, kvBoth),
                        List.of("category=beer", "origin=pl"),
                        false,
                        List.of(id2_1, id2_2, id2_3)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "hashtag matchAll true",
                        List.of(tagBeer, tagOther),
                        List.of("#beer"),
                        true,
                        List.of(id3)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "hashtag matchAll false",
                        List.of(tagBeer2, tagWine, tagNo),
                        List.of("#beer", "#wine"),
                        false,
                        List.of(id4_1, id4_2)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "mixed matchAll true",
                        List.of(both, onlyCategory, onlyTag),
                        List.of("category=beer", "#beer"),
                        true,
                        List.of(id5)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "mixed matchAll false",
                        List.of(both, onlyCategory, onlyTag),
                        List.of("category=beer", "#beer"),
                        false,
                        List.of(id5, id5_1, id5_2)
                )
        );
    }
}
