package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAuthorDto(
        @NotBlank(message = "Author name must not be blank") String name,
        @NotBlank(message = "Author surname must not be blank") String surname,
        @NotNull(message = "Country ID must not be null") Long countryId
) {

}