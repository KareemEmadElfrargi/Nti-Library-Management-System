package org.example.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.demo.entity.Author;
import org.example.demo.entity.Book;

import java.util.ArrayList;
import java.util.List;

public class CriteriaQueryRunner {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        try {
            findBooks(em, "The Hobbit", null);
            findBooks(em, null, "J.R.R. Tolkien");
            findBooks(em, "The Hobbit", "J.R.R. Tolkien");
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void findBooks(EntityManager em, String title, String authorName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();

        if (title != null) {
            predicates.add(cb.equal(book.get("title"), title));
        }

        if (authorName != null) {
            Join<Book, Author> author = book.join("author");
            predicates.add(cb.equal(author.get("name"), authorName));
        }

        cq.select(book).where(predicates.toArray(new Predicate[0]));

        TypedQuery<Book> query = em.createQuery(cq);
        List<Book> books = query.getResultList();

        System.out.println("Books matching title=" + title + ", author=" + authorName + ":");
        for (Book b : books) {
            System.out.println(" - " + b.getTitle());
        }
    }
}
