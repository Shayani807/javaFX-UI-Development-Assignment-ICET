package model;

import dto.BookDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookModel {

    public static boolean saveBook(BookDTO bookDTO) throws SQLException {

        return false;
    }

    public static List<BookDTO> getAllBooks() throws SQLException {

        return new ArrayList<>();
    }
}