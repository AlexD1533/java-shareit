package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemJpaRepositoryTest {

    @Autowired
    private ItemJpaRepository itemRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private User owner;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        // Создаем владельца
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        // Создаем другого пользователя
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        // Создаем вещи
        item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Powerful electric drill");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Screwdriver");
        item2.setDescription("Electric screwdriver");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2 = itemRepository.save(item2);

        item3 = new Item();
        item3.setName("Hammer");
        item3.setDescription("Steel hammer");
        item3.setAvailable(false);
        item3.setOwner(otherUser);
        item3 = itemRepository.save(item3);
    }

    @Test
    void findAllByOwnerId_ShouldReturnItemsForOwner() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId());

        assertThat(items, hasSize(2));
        assertThat(items, containsInAnyOrder(item1, item2));
    }

    @Test
    void findAllByOwnerId_WithNoItems_ShouldReturnEmptyList() {
        List<Item> items = itemRepository.findAllByOwnerId(999L);

        assertThat(items, empty());
    }

    @Test
    void findAllByText_ShouldReturnMatchingItems() {
        List<Item> items = itemRepository.findAllByText("drill");

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getName(), equalTo("Drill"));
    }

    @Test
    void findAllByText_ShouldSearchInDescription() {
        List<Item> items = itemRepository.findAllByText("electric");

        assertThat(items, hasSize(2));
        assertThat(items, containsInAnyOrder(item1, item2));
    }

    @Test
    void findAllByText_ShouldBeCaseInsensitive() {
        List<Item> items = itemRepository.findAllByText("DRILL");

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getName(), equalTo("Drill"));
    }

    @Test
    void findAllByText_ShouldOnlyReturnAvailableItems() {
        List<Item> items = itemRepository.findAllByText("hammer");

        assertThat(items, empty()); // hammer is not available
    }

    @Test
    void findAllByText_WithPartialText_ShouldReturnMatches() {
        List<Item> items = itemRepository.findAllByText("dril");

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getName(), equalTo("Drill"));
    }

    @Test
    void findAllByText_WithBlankText_ShouldReturnEmptyList() {
        List<Item> items = itemRepository.findAllByText("   ");

        assertThat(items, empty());
    }

    @Test
    void findAllByText_WithNoMatches_ShouldReturnEmptyList() {
        List<Item> items = itemRepository.findAllByText("nonexistent");

        assertThat(items, empty());
    }

    @Test
    void save_ShouldSetId() {
        Item newItem = new Item();
        newItem.setName("New Item");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);
        newItem.setOwner(owner);

        Item savedItem = itemRepository.save(newItem);

        assertThat(savedItem.getId(), notNullValue());
        assertThat(savedItem.getName(), equalTo("New Item"));
    }

    @Test
    void findById_ShouldReturnItem_WhenExists() {
        Item found = itemRepository.findById(item1.getId()).orElse(null);

        assertThat(found, notNullValue());
        assertThat(found.getName(), equalTo("Drill"));
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Item> found = itemRepository.findById(999L);

        assertThat(found.isPresent(), is(false));
    }

    @Test
    void delete_ShouldRemoveItem() {
        itemRepository.delete(item1);

        List<Item> items = itemRepository.findAllByOwnerId(owner.getId());
        assertThat(items, hasSize(1));
        assertThat(items.get(0).getId(), equalTo(item2.getId()));
    }
}