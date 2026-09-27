package org.example.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.demo.entity.Book;

import java.util.List;

public class CriteriaQueryRunner {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        try {
            findBooksByTitle(em, "The Hobbit");
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void findBooksByTitle(EntityManager em, String title) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        cq.select(book).where(cb.equal(book.get("title"), title));

        TypedQuery<Book> query = em.createQuery(cq);
        List<Book> books = query.getResultList();

        System.out.println("Books with title " + title + ":");
        for (Book b : books) {
            System.out.println(" - " + b.getTitle());
        }
    }
}
