package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
            ORDER BY b.end DESC
            """)
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId AND b.end <= :date
            ORDER BY b.end DESC
            """)
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId, @Param("date") LocalDateTime date);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId AND b.start >= :date
            ORDER BY b.end DESC
            """)
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId, @Param("date") LocalDateTime date);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId AND b.start <= :date AND b.end >= :date
            ORDER BY b.end DESC
            """)
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("date") LocalDateTime date);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId AND status = :status
            ORDER BY b.end DESC
            """)
    List<Booking> findWaitingOrRejectedByBookerId(@Param("bookerId") Long bookerId,
                                                  @Param("status") String status);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId
            ORDER BY b.end DESC
            """)
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId AND b.end <= :date
            ORDER BY b.end DESC
            """)
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);


    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId AND b.end >= :date
            ORDER BY b.end DESC
            """)
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId AND b.start <= :date AND b.end >= :date
            ORDER BY b.end DESC
            """)
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.owner.id = :ownerId AND status = :status
            ORDER BY b.end DESC
            """)
    List<Booking> findWaitingOrRejectedByOwnerId(@Param("ownerId") Long ownerId,
                                                  @Param("status") String status);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
            AND b.item.id = :itemId
            AND b.end < :date
            ORDER BY b.end DESC
            """)
    List<Booking> getLastBookingByBookerIdAndItemId(@Param("bookerId") Long bookerId,
                                                    @Param("itemId") Long itemId,
                                                    @Param("date") LocalDateTime date);
}
