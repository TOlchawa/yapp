package com.memoritta.server.controller;

import com.memoritta.server.client.ItemRepository;
import com.memoritta.server.manager.ItemManager;
import com.memoritta.server.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemManager itemManager;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterItemWithImage_whenAllFieldsProvided_shouldReturnUuid() throws IOException {
        // Given
        String name = "Test Item";
        String note = "This is a note";
        String barcode = "1234567890";
        byte[] imageBytes = "fake-image-data".getBytes();
        MockMultipartFile picture = new MockMultipartFile("picture", "file.jpg", "image/jpeg", imageBytes);

        UUID generatedId = UUID.randomUUID();
        when(itemManager.save(any(Item.class))).thenReturn(generatedId);

        // When
        UUID result = itemController.registerItemWithImage(name, note, barcode, picture);

        // Then
        assertNotNull(result);
        assertEquals(generatedId, result);

        verify(itemManager).save(any(Item.class));
        verify(itemRepository).save(any());
    }

    @Test
    void testRegisterItemWithImage_whenNoOptionalFields_shouldStillSaveItem() throws IOException {
        // Given
        String name = "Item Only Name";
        UUID generatedId = UUID.randomUUID();

        when(itemManager.save(any(Item.class))).thenReturn(generatedId);

        // When
        UUID result = itemController.registerItemWithImage(name, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(generatedId, result);

        verify(itemManager).save(any(Item.class));
        verify(itemRepository).save(any());
    }

    @Test
    void testPingEndpointReturnsUuid() {
        // When
        UUID result = itemController.registerItemWithImage("ping");

        // Then
        assertNotNull(result);
    }
}
