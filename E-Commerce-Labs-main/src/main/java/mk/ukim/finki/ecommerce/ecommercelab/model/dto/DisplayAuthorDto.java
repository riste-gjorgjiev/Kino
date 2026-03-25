package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;


import java.time.LocalDateTime;

public record DisplayAuthorDto(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                               String name, String surname, mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayCountryDto country) {
    public static mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayAuthorDto from(Author author){
        return new mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayAuthorDto(
                author.getId(), author.getCreatedAt(), author.getUpdatedAt(),
                author.getName(), author.getSurname(),
                DisplayCountryDto.from(author.getCountry())
        );
    }
}
