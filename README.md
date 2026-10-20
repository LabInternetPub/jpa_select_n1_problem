# n1-demo — The N+1 select problem

Console application (no web layer) that reproduces the N+1 problem with the
`Author 1—N Book` model of `../n1_problem.md` and `../n1_exit_ticket.md`.
Spring Boot 4.1.1 · Hibernate ORM 7.4.5 · H2 in memory · Java 21+.

```bash
mvn spring-boot:run
```

At startup it inserts 20 authors × 5 books, then runs seven scenarios, each in its own
read-only transaction (empty persistence context). For each one it logs every SQL
statement with its bound parameters and the number of statements counted by
Hibernate `Statistics`.

## Measured results (2026-10-20)

| # | Scenario (`LibraryService`) | `Book.author` LAZY | `Book.author` EAGER | LAZY + `default_batch_fetch_size=10` |
|---|---|---:|---:|---:|
| 1 | `findAll()` + `getAuthor().getName()` | 21 | 21 | 3 |
| 2 | `findAll()` + `getAuthor().getId()` | 1 | 21 | 1 |
| 3 | `join fetch` | 1 | 1 | 1 |
| 4 | `Author.findAll()` + `getBooks().size()` | 21 | 21 | 3 |
| 5 | `@EntityGraph(attributePaths = "books")` | 1 | 1 | 1 |
| 6 | `join` without `fetch` | 21 | 21 | 3 |
| 7 | DTO projection `select new AuthorSummary(a.name, count(b))` | 1 | 1 | 1 |

## Things to try

- **EAGER does not fix it.** In `Book.java` change `FetchType.LAZY` to `FetchType.EAGER`
  and rerun: scenario 2 goes from 1 to 21 statements, without the code ever reading the author.
- **Batch fetching.** Uncomment `hibernate.default_batch_fetch_size` in
  `application.properties`, or run
  `mvn spring-boot:run -Dspring-boot.run.arguments=--spring.jpa.properties.hibernate.default_batch_fetch_size=10`.
  The 20 per-row queries become 2 queries with `where id in (...)`.
- **Read the log.** In scenario 1 look for the same
  `select ... from author a1_0 where a1_0.id=?` repeated, with the bound value
  (`binding parameter (1:BIGINT) <- [n]`) changing each time.

- **Projection instead of entities.** Scenario 7 asks only for what the use case
  shows (name and book count). The database does the counting
  (`select a1_0.name,count(b1_0.id) ... group by a1_0.id,a1_0.name`) and no `Author`
  or `Book` entity enters the persistence context, so there is nothing lazy left to
  trigger. Trade-off: the result is read-only and cannot be navigated or modified.

## Properties used to see the problem

| Property | Purpose |
|---|---|
| `logging.level.org.hibernate.SQL=DEBUG` | Logs every SQL statement |
| `logging.level.org.hibernate.orm.jdbc.bind=TRACE` | Logs the value bound to each `?` |
| `spring.jpa.properties.hibernate.format_sql=false` | One line per statement: repetition is easy to spot |
| `spring.jpa.properties.hibernate.generate_statistics=true` | Enables `Statistics` and logs "n JDBC statements executed" per session |
