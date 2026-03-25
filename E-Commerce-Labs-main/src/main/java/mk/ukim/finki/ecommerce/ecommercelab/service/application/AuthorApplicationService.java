package mk.ukim.finki.ecommerce.ecommercelab.service.application;

import mk.ukim.finki.ecommerce.ecommercelab.model.dto.CreateAuthorDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayAuthorDto;

import java.util.List;

public interface AuthorApplicationService {
    List<DisplayAuthorDto> findAll();
    DisplayAuthorDto findById(Long id);
    DisplayAuthorDto create(CreateAuthorDto id);
}
