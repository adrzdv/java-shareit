package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select items.owner_id from bookings b left join items on items.id = b.item_id " +
            "where b.id = :idBooking", nativeQuery = true)
    long getOwnerIdByBookingId(long idBooking);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update bookings set status = :bookingStatus where id = :idBooking", nativeQuery = true)
    void setNewStatus(long idBooking, String bookingStatus);

    List<Booking> findByBooker_Id(long id);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b left join items on b.item_id = items.id where items.owner_id = :idOwner", nativeQuery = true)
    List<Booking> getBookingsByOwnerId(long idOwner);

    List<Booking> findByItem_id(long id);

}
