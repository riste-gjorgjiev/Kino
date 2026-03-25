package mk.ukim.finki.ecommerce.ecommercelab.service.application;

import mk.ukim.finki.ecommerce.ecommercelab.model.dto.CreateBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookExtendedDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;
import mk.ukim.finki.ecommerce.ecommercelab.model.projection.BookShortProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookApplicationService {
    DisplayBookDto findById(Long id);
    DisplayBookDto create(CreateBookDto dto);
    DisplayBookDto update(Long id, CreateBookDto dto);
    void delete(Long id);
    DisplayBookDto rent(Long id);

    Page<BookShortProjection> findAllFiltered(BookCategory category, BookState state, Long authorId,
                                              boolean availableOnly, Pageable pageable);
    List<DisplayBookExtendedDto> findAllExtended();
}
