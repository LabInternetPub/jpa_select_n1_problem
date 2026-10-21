package cat.tecnocampus.n1demo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Fix: entity graph on a derived query -> Hibernate adds a left join to book
    @EntityGraph(attributePaths = "books")
    List<Author> findByNameStartingWith(String prefix);

    // Fix: DTO projection -> the database counts, no Book entity is loaded
    @Query("""
           select new cat.tecnocampus.n1demo.AuthorSummary(a.name, count(b))
           from Author a left join a.books b
           group by a.id, a.name
           """)
    List<AuthorSummary> findSummaries();
}
