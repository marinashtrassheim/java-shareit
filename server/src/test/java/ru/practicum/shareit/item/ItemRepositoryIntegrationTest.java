package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.ItemRequestEntity;
import ru.practicum.shareit.user.UserEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private UserEntity owner;
    private UserEntity anotherUser;
    private ItemRequestEntity request;

    @BeforeEach
    void setUp() {
        owner = UserEntity.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        entityManager.persist(owner);

        anotherUser = UserEntity.builder()
                .name("Another")
                .email("another@example.com")
                .build();
        entityManager.persist(anotherUser);

        request = ItemRequestEntity.builder()
                .description("Need a drill")
                .requester(anotherUser)
                .build();
        entityManager.persist(request);
    }

    @Test
    void findByOwnerId_shouldReturnItemsForOwner() {
        ItemEntity item1 = ItemEntity.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item1);

        ItemEntity item2 = ItemEntity.builder()
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item2);

        ItemEntity otherItem = ItemEntity.builder()
                .name("Saw")
                .description("Wood saw")
                .available(true)
                .owner(anotherUser)
                .build();
        entityManager.persist(otherItem);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemEntity> result = itemRepository.findByOwnerId(owner.getId(), pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemEntity::getName)
                .containsExactlyInAnyOrder("Drill", "Hammer");
        assertThat(result).extracting(item -> item.getOwner().getId())
                .containsOnly(owner.getId());
    }

    @Test
    void findByOwnerId_withPagination_shouldReturnPaginatedResults() {
        for (int i = 1; i <= 15; i++) {
            ItemEntity item = ItemEntity.builder()
                    .name("Item " + i)
                    .description("Description " + i)
                    .available(true)
                    .owner(owner)
                    .build();
            entityManager.persist(item);
        }
        entityManager.flush();

        Pageable firstPage = PageRequest.of(0, 5);
        List<ItemEntity> firstPageResult = itemRepository.findByOwnerId(owner.getId(), firstPage);
        assertThat(firstPageResult).hasSize(5);

        Pageable secondPage = PageRequest.of(1, 5);
        List<ItemEntity> secondPageResult = itemRepository.findByOwnerId(owner.getId(), secondPage);
        assertThat(secondPageResult).hasSize(5);

        assertThat(firstPageResult).extracting(ItemEntity::getName)
                .doesNotContainAnyElementsOf(secondPageResult.stream()
                        .map(ItemEntity::getName)
                        .toList());
    }

    @Test
    void searchAvailableItems_shouldReturnAvailableItemsMatchingText() {
        ItemEntity item1 = ItemEntity.builder()
                .name("Power Drill")
                .description("Cordless drill")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item1);

        ItemEntity item2 = ItemEntity.builder()
                .name("Hammer")
                .description("Heavy DUTY hammer")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item2);

        ItemEntity unavailableItem = ItemEntity.builder()
                .name("Another Drill")
                .description("Corded drill")
                .available(false)
                .owner(owner)
                .build();
        entityManager.persist(unavailableItem);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemEntity> result = itemRepository.searchAvailableItems("drill", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Power Drill");
        assertThat(result.getFirst().getAvailable()).isTrue();
    }

    @Test
    void searchAvailableItems_caseInsensitive_shouldMatchRegardlessOfCase() {
        ItemEntity item1 = ItemEntity.builder()
                .name("DRILL")
                .description("POWER DRILL")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item1);

        ItemEntity item2 = ItemEntity.builder()
                .name("drill")
                .description("small drill")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item2);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemEntity> result = itemRepository.searchAvailableItems("DrIlL", pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemEntity::getName)
                .containsExactlyInAnyOrder("DRILL", "drill");
    }

    @Test
    void searchAvailableItems_shouldSearchInNameAndDescription() {
        ItemEntity item1 = ItemEntity.builder()
                .name("Tool")
                .description("Electric drill")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item1);

        ItemEntity item2 = ItemEntity.builder()
                .name("Drill machine")
                .description("Tool")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item2);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemEntity> drillResults = itemRepository.searchAvailableItems("drill", pageable);
        assertThat(drillResults).hasSize(2);

        List<ItemEntity> toolResults = itemRepository.searchAvailableItems("tool", pageable);
        assertThat(toolResults).hasSize(2);
    }

    @Test
    void saveItemWithRequest_shouldPersistCorrectly() {
        ItemEntity item = ItemEntity.builder()
                .name("Drill")
                .description("For your request")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemEntity saved = itemRepository.save(item);
        entityManager.flush();
        entityManager.clear();

        ItemEntity found = entityManager.find(ItemEntity.class, saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Drill");
        assertThat(found.getRequest()).isNotNull();
        assertThat(found.getRequest().getId()).isEqualTo(request.getId());
        assertThat(found.getOwner().getId()).isEqualTo(owner.getId());
    }
}
