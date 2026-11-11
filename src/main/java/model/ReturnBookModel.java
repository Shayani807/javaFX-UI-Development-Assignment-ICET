package model;

import Util.HibernateUtil;
import dto.IssueDTO;
import entity.Book;
import entity.IssuedBook;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnBookModel {

    public List<IssueDTO> getBorrowedBooks() {
        List<IssueDTO> dtoList = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<IssuedBook> list = session.createQuery(
                            "FROM IssuedBook WHERE status = :status ORDER BY issueDate DESC",
                            IssuedBook.class)
                    .setParameter("status", "BORROWED")
                    .list();

            for (IssuedBook issued : list) {
                dtoList.add(new IssueDTO(
                        issued.getIssueId(),
                        issued.getMember().getMemberId(),
                        issued.getBook().getIsbn(),
                        issued.getBook().getTitle(),
                        issued.getIssueDate(),
                        issued.getDueDate(),
                        issued.getReturnDate(),
                        issued.getStatus()
                ));
            }
        }
        return dtoList;
    }

    public boolean returnBook(String issueId, LocalDate returnDate) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            IssuedBook issued = session.get(IssuedBook.class, issueId);
            if (issued == null) return false;
            if ("RETURNED".equals(issued.getStatus())) return false;

            issued.setReturnDate(returnDate);
            issued.setStatus("RETURNED");
            session.merge(issued);

            Book book = issued.getBook();
            if (book != null) {
                // FIX: availableQuantity
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
                session.merge(book);
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        }
    }
}