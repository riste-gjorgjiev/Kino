package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

public record DisplayPopularBookDto(String bookName, Long rentCount) {
    public static DisplayPopularBookDto from(Object[] row){
        return new DisplayPopularBookDto((String) row[0], (Long) row[1]);
    }
}
