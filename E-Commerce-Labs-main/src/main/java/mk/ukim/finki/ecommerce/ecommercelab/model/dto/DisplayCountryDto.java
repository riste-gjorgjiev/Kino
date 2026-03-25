package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Country;

public record DisplayCountryDto(Long id, String name, String continent) {
    public static mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayCountryDto from (Country country){
        return new mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayCountryDto(country.getId(), country.getName(), country.getContinent());
    }
}
