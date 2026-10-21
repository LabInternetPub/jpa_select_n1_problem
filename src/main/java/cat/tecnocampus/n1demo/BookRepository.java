package cat.tecnocampus.n1demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Fix: fetch join -> books and their authors in one SELECT
    @Query("select b from Book b join fetch b.author")
    List<Book> findAllWithAuthor();

    // NOT a fix: join without fetch -> author is still an uninitialized proxy
    @Query("select b from Book b join b.author a")
    List<Book> findAllJoinWithoutFetch();
}
