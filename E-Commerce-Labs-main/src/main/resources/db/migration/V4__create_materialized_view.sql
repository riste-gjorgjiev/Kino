CREATE VIEW mv_book_category_stats AS
SELECT
    b.category,
    COUNT(*)                                    AS total_books,
    SUM(b.available_copies)                     AS total_available_copies,
    COUNT(CASE WHEN b.state = 'BAD' THEN 1 END) AS books_in_bad_condition
FROM books b
GROUP BY b.category;