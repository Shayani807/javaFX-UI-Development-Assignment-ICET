package model;

import dto.BookDTO;
import entity.Book;
import Util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class BookModel {

    // =========================================
    // SAVE BOOK
    // =========================================
    public boolean saveBook(BookDTO dto) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            // Check duplicate ISBN
            Book existingBook = session.get(Book.class, dto.getId());

            if (existingBook != null) {
                transaction.rollback();
                System.out.println("Book ISBN Already Exists!");
                return false;
            }

            Book book = new Book(
                    dto.getId(),
                    dto.getTitle(),
                    dto.getAuthor(),
                    dto.getCategory(),
                    Integer.parseInt(dto.getPublishedYear()),
                    dto.getQuantity()
            );

            session.persist(book);

            transaction.commit();

            System.out.println("BOOK SAVED SUCCESSFULLY!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();
            return false;
        }
    }


    // =========================================
    // GET ALL BOOKS
    // =========================================
    public List<BookDTO> getAllBooks() {

        List<BookDTO> bookList = new ArrayList<>();

        try (Session session = HibernateUtil.getSession()) {

            List<Book> books = session.createQuery(
                    "FROM Book ORDER BY title", Book.class
            ).getResultList();

            for (Book book : books) {

                BookDTO dto = new BookDTO(
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        String.valueOf(book.getPublishedYear()),
                        book.getQuantity()
                );

                bookList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookList;
    }


    // =========================================
    // SEARCH BOOK
    // =========================================
    public List<BookDTO> searchBooks(String searchText) {

        List<BookDTO> bookList = new ArrayList<>();

        try (Session session = HibernateUtil.getSession()) {

            String search = "%" + searchText.toLowerCase() + "%";

            List<Book> books = session.createQuery(
                            "FROM Book b WHERE " +
                                    "LOWER(b.isbn) LIKE :search OR " +
                                    "LOWER(b.title) LIKE :search OR " +
                                    "LOWER(b.author) LIKE :search OR " +
                                    "LOWER(b.category) LIKE :search",
                            Book.class
                    )
                    .setParameter("search", search)
                    .getResultList();

            for (Book book : books) {

                BookDTO dto = new BookDTO(
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        String.valueOf(book.getPublishedYear()),
                        book.getQuantity()
                );

                bookList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookList;
    }


    // =========================================
    // UPDATE BOOK
    // =========================================
    public boolean updateBook(BookDTO dto) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            Book book = session.get(Book.class, dto.getId());

            if (book == null) {
                transaction.rollback();
                System.out.println("Book Not Found!");
                return false;
            }

            int newQuantity = dto.getQuantity();

            // Number of copies currently issued
            int issuedQuantity =
                    book.getQuantity() - book.getAvailableQuantity();

            // Prevent reducing quantity below issued copies
            if (newQuantity < issuedQuantity) {
                transaction.rollback();

                System.out.println(
                        "Cannot reduce quantity below issued copies!"
                );

                return false;
            }

            book.setTitle(dto.getTitle());
            book.setAuthor(dto.getAuthor());
            book.setCategory(dto.getCategory());
            book.setPublishedYear(
                    Integer.parseInt(dto.getPublishedYear())
            );

            book.setQuantity(newQuantity);

            // Preserve currently issued copies
            book.setAvailableQuantity(
                    newQuantity - issuedQuantity
            );

            transaction.commit();

            System.out.println("BOOK UPDATED SUCCESSFULLY!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();
            return false;
        }
    }


    // =========================================
    // DELETE BOOK
    // =========================================
    public boolean deleteBook(String isbn) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            Book book = session.get(Book.class, isbn);

            if (book == null) {
                transaction.rollback();
                System.out.println("Book Not Found!");
                return false;
            }

            // Do not delete books that are currently issued
            if (book.getAvailableQuantity() < book.getQuantity()) {
                transaction.rollback();

                System.out.println(
                        "Cannot delete! This book has issued copies."
                );

                return false;
            }

            session.remove(book);

            transaction.commit();

            System.out.println("BOOK DELETED SUCCESSFULLY!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();
            return false;
        }
    }


    // =========================================
    // GET TOTAL BOOKS COUNT
    // =========================================
    public long getTotalBooksCount() {

        try (Session session = HibernateUtil.getSession()) {

            return session.createQuery(
                    "SELECT COUNT(b) FROM Book b",
                    Long.class
            ).getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    // =========================================
    // GET AVAILABLE BOOKS COUNT
    // Total available copies
    // =========================================
    public long getAvailableBooksCount() {

        try (Session session = HibernateUtil.getSession()) {

            Long count = session.createQuery(
                    "SELECT SUM(b.availableQuantity) FROM Book b",
                    Long.class
            ).getSingleResult();

            return count != null ? count : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    // =========================================
    // GET ISSUED BOOKS COUNT
    // =========================================
    public long getIssuedBooksCount() {

        try (Session session = HibernateUtil.getSession()) {

            Long count = session.createQuery(
                    "SELECT SUM(b.quantity - b.availableQuantity) " +
                            "FROM Book b",
                    Long.class
            ).getSingleResult();

            return count != null ? count : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}