package mk.ukim.finki.ecommerce.ecommercelab.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "authors")
public class Author extends BaseAuditableEntity{
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public Author(String name, String surname, Country country) {
        this.name = name;
        this.surname = surname;
        this.country = country;
    }
}
