package cat.tecnocampus.n1demo;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // Try FetchType.EAGER (the JPA default for @ManyToOne) and rerun scenario 2:
    // findAll() alone then executes 1 + 20 queries
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    protected Book() { }

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
        author.getBooks().add(this);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Author getAuthor() { return author; }
}
