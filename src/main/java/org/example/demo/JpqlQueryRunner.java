package org.example.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.demo.entity.Book;

import java.util.List;

public class JpqlQueryRunner {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        try {
            findBooksByAuthorName(em, "J.R.R. Tolkien");
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
}
