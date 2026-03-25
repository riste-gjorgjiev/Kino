package mk.ukim.finki.ecommerce.ecommercelab.service.application;


import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Country;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.CreateAuthorDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayAuthorDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.exceptions.AuthorNotFoundException;
import mk.ukim.finki.ecommerce.ecommercelab.model.exceptions.CountryNotFoundException;
import mk.ukim.finki.ecommerce.ecommercelab.repository.CountryRepository;
import mk.ukim.finki.ecommerce.ecommercelab.service.domain.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorApplicationServiceImpl implements AuthorApplicationService{
    private final AuthorService authorService;
    private final CountryRepository countryRepository;

    public AuthorApplicationServiceImpl(AuthorService authorService, CountryRepository countryRepository) {
        this.authorService = authorService;
        this.countryRepository = countryRepository;
    }

    @Override
    public List<DisplayAuthorDto> findAll() {
        return authorService.findAll().stream().map(DisplayAuthorDto::from).toList();
    }

    @Override
    public DisplayAuthorDto findById(Long id) {
        return authorService.findById(id)
                .map(DisplayAuthorDto::from)
                .orElseThrow(() ->  new AuthorNotFoundException(id));
    }

    @Override
    public DisplayAuthorDto create(CreateAuthorDto dto) {
        Country country = countryRepository.findById(dto.countryId())
                .orElseThrow(() -> new CountryNotFoundException(dto.countryId()));
        Author author = authorService.create(dto.name(), dto.surname(), country);
        return DisplayAuthorDto.from(author);
    }
}
