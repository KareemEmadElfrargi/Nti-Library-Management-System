package org.example.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;
import org.example.demo.entity.Author;
import org.example.demo.entity.Book;
import org.example.demo.entity.Category;
import org.example.demo.entity.Customer;
import org.example.demo.entity.Employee;
import org.example.demo.entity.Publisher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SampleDataRunner {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Author tolkien = new Author("J.R.R. Tolkien");
            Author orwell = new Author("George Orwell");

            Publisher houghton = new Publisher("Houghton Mifflin");
            Publisher secker = new Publisher("Secker & Warburg");

            Category fantasy = new Category("Fantasy");
            Category dystopian = new Category("Dystopian");
            Category classics = new Category("Classics");

            Book hobbit = new Book("The Hobbit");
            hobbit.setPublisher(houghton);
            tolkien.addBook(hobbit);
            hobbit.addCategory(fantasy);
            hobbit.addCategory(classics);

            Book lotr = new Book("The Lord of the Rings");
            lotr.setPublisher(houghton);
            tolkien.addBook(lotr);
            lotr.addCategory(fantasy);

            Book nineteenEightyFour = new Book("1984");
            nineteenEightyFour.setPublisher(secker);
            orwell.addBook(nineteenEightyFour);
            nineteenEightyFour.addCategory(dystopian);
            nineteenEightyFour.addCategory(classics);

            em.persist(houghton);
            em.persist(secker);
            em.persist(fantasy);
            em.persist(dystopian);
            em.persist(classics);
            em.persist(tolkien);
            em.persist(orwell);

            Employee cashier = new Employee("Mohamed Alaa", "Cashier");
            Employee manager = new Employee("Mohamed Ezz", "Store Manager");
            Customer loyalCustomer = new Customer("Kareem Emad", "GOLD");
            Customer newCustomer = new Customer("Uosef Emad", "BRONZE");

            em.persist(cashier);
            em.persist(manager);
            em.persist(loyalCustomer);
            em.persist(newCustomer);

            em.getTransaction().commit();

            inspectSchema(em);
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void inspectSchema(EntityManager em) {
        em.unwrap(Session.class).doWork(connection -> {
            printTables(connection);
            printForeignKeys(connection);
        });
    }

    private static void printTables(Connection connection) throws SQLException {
        System.out.println("----- Tables -----");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                             "WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME")) {
            while (rs.next()) {
                System.out.println(" - " + rs.getString("TABLE_NAME"));
            }
        }
        System.out.println();
    }

    private static void printForeignKeys(Connection connection) throws SQLException {
        System.out.println("=== Foreign Keys ===");
        String sql =
                "SELECT rc.CONSTRAINT_NAME AS FK_NAME, " +
                        "       kcu.TABLE_NAME AS FK_TABLE, kcu.COLUMN_NAME AS FK_COLUMN, " +
                        "       kcu2.TABLE_NAME AS PK_TABLE, kcu2.COLUMN_NAME AS PK_COLUMN " +
                        "FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc " +
                        "JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu " +
                        "  ON kcu.CONSTRAINT_NAME = rc.CONSTRAINT_NAME " +
                        "JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu2 " +
                        "  ON kcu2.CONSTRAINT_NAME = rc.UNIQUE_CONSTRAINT_NAME " +
                        "ORDER BY FK_TABLE, FK_COLUMN";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf(
                        " - %s.%s -> %s.%s (%s)%n",
                        rs.getString("FK_TABLE"),
                        rs.getString("FK_COLUMN"),
                        rs.getString("PK_TABLE"),
                        rs.getString("PK_COLUMN"),
                        rs.getString("FK_NAME"));
            }
        }
    }
}
