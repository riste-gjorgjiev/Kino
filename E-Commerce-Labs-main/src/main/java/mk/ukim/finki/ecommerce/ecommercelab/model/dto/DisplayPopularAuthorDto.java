package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

public record DisplayPopularAuthorDto(String authorName, Long rentCount) {
    public static DisplayPopularAuthorDto from(Object[] row){
        return new DisplayPopularAuthorDto((String) row[0], (Long) row[1]);
    }
}
