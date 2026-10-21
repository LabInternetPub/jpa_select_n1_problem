package cat.tecnocampus.n1demo;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "author")          // default fetch = LAZY
    private List<Book> books = new ArrayList<>();

    protected Author() { }

    public Author(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Book> getBooks() { return books; }

    // No toString() that prints books: it would trigger the lazy load (hidden N+1)
}
