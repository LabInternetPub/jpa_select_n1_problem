package cat.tecnocampus.n1demo;

/** DTO projection: only the columns the use case needs, no managed entities. */
public record AuthorSummary(String name, long bookCount) { }
