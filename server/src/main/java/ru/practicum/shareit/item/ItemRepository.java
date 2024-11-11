package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner_id(long id);

    @Query(value = "SELECT DISTINCT * FROM ITEMS " +
            "WHERE UPPER(name) LIKE %:searchString% OR UPPER(description) LIKE %:searchString%", nativeQuery = true)
    List<Item> search(String searchString);

    List<Item> findByRequest_Id(long id);

    @Query(value = "select * from items i where i.request_id in " +
            "(select r.id from request r where r.requestor_id = :id)", nativeQuery = true)
    List<Item> findByRequestorId(long id);
}
