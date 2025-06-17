package com.memoritta.server.manager;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.dao.DescriptionDao;
import com.memoritta.server.dao.ItemDao;
import com.memoritta.server.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {ItemManagerTest.Config.class, ItemManager.class})
class ItemManagerCrudTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BinaryDataManager binaryDataManager;

    @Autowired
    private ItemManager itemManager;

    @BeforeEach
    void resetMocks() {
        reset(itemRepository, binaryDataManager);
    }

    @Test
    void saveItem_withMultipartFile_shouldSaveAndReturnId() throws Exception {
        byte[] data = "img".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", data);
        UUID picId = UUID.randomUUID();
        UUID savedId = UUID.randomUUID();
        when(binaryDataManager.save(data)).thenReturn(picId);
        when(itemRepository.save(any(ItemDao.class)))
                .thenReturn(ItemDao.builder().id(savedId).build());

        UUID result = itemManager.saveItem("name", "note", "123", file, null);

        assertThat(result).isEqualTo(savedId);
        verify(binaryDataManager).save(data);
        verify(itemRepository).save(any(ItemDao.class));
    }

    @Test
    void saveItem_withBase64_shouldSaveAndReturnId() throws Exception {
        byte[] data = "img".getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.getEncoder().encodeToString(data);
        UUID picId = UUID.randomUUID();
        UUID savedId = UUID.randomUUID();
        when(binaryDataManager.save(data)).thenReturn(picId);
        when(itemRepository.save(any(ItemDao.class)))
                .thenReturn(ItemDao.builder().id(savedId).build());

        UUID result = itemManager.saveItem("name", "note", "123", null, base64);

        assertThat(result).isEqualTo(savedId);
        verify(binaryDataManager).save(data);
        verify(itemRepository).save(any(ItemDao.class));
    }

    @Test
    void updateItem_shouldChangeFields() throws Exception {
        UUID id = UUID.randomUUID();
        ItemDao dao = ItemDao.builder()
                .id(id)
                .name("old")
                .description(DescriptionDao.builder()
                        .note("old")
                        .barcode("111")
                        .build())
                .build();
        when(itemRepository.findById(id)).thenReturn(Optional.of(dao));
        when(itemRepository.save(any(ItemDao.class))).thenReturn(dao);

        Item result = itemManager.updateItem(id.toString(), "new", "new note", "222", null, null);

        assertThat(result.getName()).isEqualTo("new");
        assertThat(result.getDescription().getNote()).isEqualTo("new note");
        assertThat(result.getDescription().getBarcode()).isEqualTo("222");
        verify(itemRepository).save(any(ItemDao.class));
    }

    @Test
    void fetchItem_returnsItem() {
        UUID id = UUID.randomUUID();
        when(itemRepository.findById(id))
                .thenReturn(Optional.of(ItemDao.builder().id(id).build()));

        Item result = itemManager.fetchItem(id.toString());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void fetchItem_returnsNull_whenNotFound() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        Item result = itemManager.fetchItem(UUID.randomUUID().toString());

        assertThat(result).isNull();
    }

    @Test
    void listAllItemIds_shouldReturnIds() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(itemRepository.findAll()).thenReturn(List.of(
                ItemDao.builder().id(id1).build(),
                ItemDao.builder().id(id2).build()
        ));

        List<UUID> result = itemManager.listAllItemIds();

        assertThat(result).containsExactly(id1, id2);
    }

    @Test
    void listItemsByBarcode_shouldReturnIds() {
        UUID id = UUID.randomUUID();
        when(itemRepository.findByDescriptionBarcode("123"))
                .thenReturn(List.of(ItemDao.builder().id(id).build()));

        List<UUID> result = itemManager.listItemsByBarcode("123");

        assertThat(result).containsExactly(id);
    }
}
