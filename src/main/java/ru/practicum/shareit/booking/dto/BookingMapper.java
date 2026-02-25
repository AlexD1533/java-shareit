package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;

import java.util.List;

public final class BookingMapper {

    public static Booking mapToBooking(BookingRequest request, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStartDate(request.getStart());
        booking.setEndDate(request.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getBookingId());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        dto.setStatus(booking.getStatus());

        User bookerInfo = new User();
        bookerInfo.setId(booking.getBooker().getId());
        bookerInfo.setName(booking.getBooker().getName());
        dto.setBooker(bookerInfo);

        Item itemInfo = new Item();
        itemInfo.setId(booking.getItem().getId());
        itemInfo.setName(booking.getItem().getName());
        dto.setItem(ItemMapper.mapToItemSmallDto(itemInfo));

        return dto;
    }


    public static List<BookingDto> mapToBookingDtoToList(List<Booking> all) {
        return all.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }
}