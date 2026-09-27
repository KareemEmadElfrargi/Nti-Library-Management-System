package org.example.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.demo.entity.Author;
import org.example.demo.entity.Book;
import org.hibernate.LazyInitializationException;

import java.util.List;

public class JpqlQueryRunner {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        try {
            findBooksByAuthorName(em, "J.R.R. Tolkien");
            findBooksByPublisherName(em, "Houghton Mifflin");
            fetchAuthorWithBooks(em, "J.R.R. Tolkien");
            countBooksPerAuthor(em);
            compareLazyVsJoinFetch(emf, "J.R.R. Tolkien");
            findBooksByCategoryName(em, "Fantasy");
            findAuthorByNameHqlShorthand(em, "J.R.R. Tolkien");
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void findBooksByAuthorName(EntityManager em, String authorName) {
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b JOIN FETCH b.author a WHERE a.name = :authorName",
                Book.class);
        query.setParameter("authorName", authorName);

        List<Book> books = query.getResultList();

        System.out.println("Books by " + authorName + ":");
        for (Book book : books) {
            System.out.println(" - " + book.getTitle());
        }
    }

    private static void findBooksByPublisherName(EntityManager em, String publisherName) {
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b JOIN FETCH b.publisher p WHERE p.name = :publisherName",
                Book.class);
        query.setParameter("publisherName", publisherName);

        List<Book> books = query.getResultList();

        System.out.println("Books published by " + publisherName + ":");
        for (Book book : books) {
            System.out.println(" - " + book.getTitle());
        }
    }

    private static void fetchAuthorWithBooks(EntityManager em, String authorName) {
        TypedQuery<Author> query = em.createQuery(
                "SELECT a FROM Author a JOIN FETCH a.books WHERE a.name = :authorName",
                Author.class);
        query.setParameter("authorName", authorName);

        Author author = query.getSingleResult();

        System.out.println("Author " + author.getName() + " and their books:");
        for (Book book : author.getBooks()) {
            System.out.println(" - " + book.getTitle());
        }
    }

    private static void countBooksPerAuthor(EntityManager em) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT a.name, COUNT(b) FROM Author a JOIN a.books b GROUP BY a.name",
                Object[].class);

        List<Object[]> results = query.getResultList();

        System.out.println("Book count per author:");
        for (Object[] row : results) {
            System.out.println(" - " + row[0] + ": " + row[1]);
        }
    }

    private static void compareLazyVsJoinFetch(EntityManagerFactory emf, String authorName) {

        Author lazyAuthor;
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Author> query = em.createQuery(
                    "SELECT a FROM Author a WHERE a.name = :authorName",
                    Author.class);
            query.setParameter("authorName", authorName);
            lazyAuthor = query.getSingleResult();

        }

        try {
            lazyAuthor.getBooks().size();
            System.out.println("Books read without error (unexpected).");
        } catch (LazyInitializationException e) {
            System.out.println("LazyInitializationException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("----- JOIN FETCH query -----");
        Author fetchedAuthor;
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Author> query = em.createQuery(
                    "SELECT a FROM Author a JOIN FETCH a.books WHERE a.name = :authorName",
                    Author.class);
            query.setParameter("authorName", authorName);
            fetchedAuthor = query.getSingleResult();
        }

        System.out.println("Books read after close: ");
        for (Book book : fetchedAuthor.getBooks()) {
            System.out.println(" - " + book.getTitle());
        }
    }

    private static void findBooksByCategoryName(EntityManager em, String categoryName) {
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b JOIN b.categories c WHERE c.name = :categoryName",
                Book.class);
        query.setParameter("categoryName", categoryName);

        List<Book> books = query.getResultList();

        System.out.println("Books in category " + categoryName + ":");
        for (Book book : books) {
            System.out.println(" - " + book.getTitle());
        }
    }

    private static void findAuthorByNameHqlShorthand(EntityManager em, String authorName) {
        TypedQuery<Author> query = em.createQuery(
                "FROM Author a WHERE a.name = :authorName",
                Author.class);
        query.setParameter("authorName", authorName);

        Author author = query.getSingleResult();

        System.out.println(author.getName());
    }
}
