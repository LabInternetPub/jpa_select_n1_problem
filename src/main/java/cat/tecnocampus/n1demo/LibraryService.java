package cat.tecnocampus.n1demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Each method runs in its own transaction, so it starts with an empty
 * persistence context: nothing is cached from a previous scenario.
 */
@Service
@Transactional(readOnly = true)
public class LibraryService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public LibraryService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /** Exit ticket code: N+1 on a LAZY @ManyToOne. */
    public List<String> labels() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                    .map(b -> b.getTitle() + " by " + b.getAuthor().getName())
                    .toList();
    }

    /** Only the id of the author: the proxy already holds it, no extra query. */
    public List<Long> authorIds() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                    .map(b -> b.getAuthor().getId())
                    .toList();
    }

    /** Fix: join fetch. */
    public List<String> labelsJoinFetch() {
        List<Book> books = bookRepository.findAllWithAuthor();
        return books.stream()
                    .map(b -> b.getTitle() + " by " + b.getAuthor().getName())
                    .toList();
    }

    /** N+1 on a LAZY @OneToMany. */
    public List<String> bookCounts() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                      .map(a -> a.getName() + ": " + a.getBooks().size())
                      .toList();
    }

    /** Fix: @EntityGraph. */
    public List<String> bookCountsEntityGraph() {
        List<Author> authors = authorRepository.findByNameStartingWith("Author");
        return authors.stream()
                      .map(a -> a.getName() + ": " + a.getBooks().size())
                      .toList();
    }

    /** Fix: DTO projection. Counted in SQL; authors and books are not loaded as entities. */
    public List<String> bookCountsProjection() {
        List<AuthorSummary> summaries = authorRepository.findSummaries();
        return summaries.stream()
                        .map(s -> s.name() + ": " + s.bookCount())
                        .toList();
    }

    /** Common mistake: join without fetch does not initialize the association. */
    public List<String> labelsJoinWithoutFetch() {
        List<Book> books = bookRepository.findAllJoinWithoutFetch();
        return books.stream()
                    .map(b -> b.getTitle() + " by " + b.getAuthor().getName())
                    .toList();
    }
}
