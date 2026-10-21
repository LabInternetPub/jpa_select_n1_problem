package cat.tecnocampus.n1demo;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 20 authors x 5 books = 100 books, the numbers used in the exit ticket.
 */
@Component
public class DataLoader {

    static final int AUTHORS = 20;
    static final int BOOKS_PER_AUTHOR = 5;

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public DataLoader(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void load() {
        for (int i = 1; i <= AUTHORS; i++) {
            Author author = authorRepository.save(new Author("Author " + i));
            for (int j = 1; j <= BOOKS_PER_AUTHOR; j++) {
                bookRepository.save(new Book("Book " + i + "." + j, author));
            }
        }
    }
}
