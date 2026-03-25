package mk.ukim.finki.ecommerce.ecommercelab.service.domain;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Country;
import mk.ukim.finki.ecommerce.ecommercelab.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService{
    private final AuthorRepository authorRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public Author create(String name, String surname, Country country) {
        return authorRepository.save(new Author(name, surname, country));
    }
}
