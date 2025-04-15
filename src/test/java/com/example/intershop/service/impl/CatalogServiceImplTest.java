package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    private ItemRepository itemRepository;
    private CatalogService catalogService;

    private Item item1, item2;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        catalogService = new CatalogServiceImpl(itemRepository);

        item1 = new Item();
        item1.setId(1L);
        item1.setTitle("title1");
        item1.setDescription("desc1");
        item1.setPrice(BigDecimal.valueOf(50));

        item2 = new Item();
        item2.setId(2L);
        item2.setTitle("title2");
        item2.setDescription("desc2");
        item2.setPrice(BigDecimal.valueOf(30));
    }

    @Test
    void getItems_noSearch_shouldReturnAllItems() {
        when(itemRepository.findAll()).thenReturn(Flux.just(item1, item2));

        StepVerifier.create(catalogService.getItems("", "NO", 1, 10))
                .expectNext(item1, item2)
                .verifyComplete();
    }

    @Test
    void getItems_withSearch_shouldFilter() {
        when(itemRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("title2", "title2", Sort.unsorted()))
                .thenReturn(Flux.just(item2));

        StepVerifier.create(catalogService.getItems("title2", "NO", 1, 10))
                .expectNext(item2)
                .verifyComplete();
    }

    @Test
    void getItemById_found_shouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));

        StepVerifier.create(catalogService.getItemById(1L))
                .expectNextMatches(item -> item.getTitle().equals("title1"))
                .verifyComplete();
    }

    @Test
    void getItemById_notFound_shouldReturnEmpty() {
        when(itemRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(catalogService.getItemById(99L))
                .verifyComplete();
    }
}
