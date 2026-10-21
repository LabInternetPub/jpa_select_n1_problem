package cat.tecnocampus.n1demo;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Runs every scenario and counts the SQL statements each one executes.
 */
@Component
public class DemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoRunner.class);

    private final DataLoader dataLoader;
    private final LibraryService service;
    private final Statistics statistics;
    private final List<String> summary = new ArrayList<>();

    public DemoRunner(DataLoader dataLoader, LibraryService service, EntityManagerFactory emf) {
        this.dataLoader = dataLoader;
        this.service = service;
        this.statistics = emf.unwrap(SessionFactory.class).getStatistics();
    }

    @Override
    public void run(String... args) {
        dataLoader.load();

        scenario("1. labels(): findAll() + getAuthor().getName()", service::labels);
        scenario("2. authorIds(): findAll() + getAuthor().getId()", service::authorIds);
        scenario("3. labelsJoinFetch(): join fetch", service::labelsJoinFetch);
        scenario("4. bookCounts(): findAll() + getBooks().size()", service::bookCounts);
        scenario("5. bookCountsEntityGraph(): @EntityGraph", service::bookCountsEntityGraph);
        scenario("6. labelsJoinWithoutFetch(): join, no fetch", service::labelsJoinWithoutFetch);
        scenario("7. bookCountsProjection(): DTO projection", service::bookCountsProjection);

        log.info("");
        log.info("================ SUMMARY ({} authors, {} books) ================",
                 DataLoader.AUTHORS, DataLoader.AUTHORS * DataLoader.BOOKS_PER_AUTHOR);
        summary.forEach(log::info);
    }

    private void scenario(String name, Supplier<List<?>> body) {
        log.info("");
        log.info("================ {} ================", name);
        statistics.clear();
        List<?> result = body.get();
        long statements = statistics.getPrepareStatementCount();
        log.info("-> {} rows, {} SQL statements", result.size(), statements);
        summary.add(String.format("%-55s %3d SQL statements", name, statements));
    }
}
