package model;

import dto.MemberDTO;
import com.library.Util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberModel {

    public boolean saveMember(MemberDTO dto) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "INSERT INTO member (member_id, full_name, email, phone, address) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, dto.getMemberId());
        pst.setString(2, dto.getFullName());
        pst.setString(3, dto.getEmail());
        pst.setString(4, dto.getPhone());
        pst.setString(5, dto.getAddress());

        return pst.executeUpdate() > 0;
    }

    public int getTotalMembersCount() throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM member");
        if (rs.next()) { return rs.getInt(1); }
        return 0;
    }
}