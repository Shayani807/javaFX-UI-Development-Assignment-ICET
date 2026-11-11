package model;

import dto.MemberDTO;
import entity.Member;
import Util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MemberModel {

    // =========================================
    // SAVE MEMBER
    // =========================================
    public boolean saveMember(MemberDTO dto) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            Member member = new Member();

            member.setMemberId(dto.getMemberId());
            member.setFullName(dto.getFullName());
            member.setEmail(dto.getEmail());
            member.setPhone(dto.getPhone());
            member.setAddress(dto.getAddress());

            // Save using Hibernate
            session.persist(member);

            // Commit transaction
            transaction.commit();

            System.out.println("Member Saved Successfully!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            System.err.println("Member Save Failed!");
            e.printStackTrace();

            return false;
        }
    }


    // =========================================
    // GET ALL MEMBERS
    // =========================================
    public List<MemberDTO> getAllMembers() {

        List<MemberDTO> dtoList = new ArrayList<>();

        try (Session session = HibernateUtil.getSession()) {

            // Get all members using Hibernate HQL
            List<Member> members = session.createQuery(
                    "FROM Member", Member.class
            ).getResultList();

            // Convert Entity to DTO
            for (Member member : members) {

                MemberDTO dto = new MemberDTO(
                        member.getMemberId(),
                        member.getFullName(),
                        member.getEmail(),
                        member.getPhone(),
                        member.getAddress(),
                        member.getCreatedAt(),
                        member.getUpdatedAt()
                );

                dtoList.add(dto);
            }

        } catch (Exception e) {

            System.err.println("Get All Members Failed!");
            e.printStackTrace();
        }

        return dtoList;
    }


    // =========================================
    // SEARCH MEMBER
    // =========================================
    public MemberDTO searchMember(String memberId) {

        try (Session session = HibernateUtil.getSession()) {

            Member member = session.get(Member.class, memberId);

            if (member != null) {

                return new MemberDTO(
                        member.getMemberId(),
                        member.getFullName(),
                        member.getEmail(),
                        member.getPhone(),
                        member.getAddress(),
                        member.getCreatedAt(),
                        member.getUpdatedAt()
                );
            }

        } catch (Exception e) {

            System.err.println("Search Member Failed!");
            e.printStackTrace();
        }

        return null;
    }


    // =========================================
    // UPDATE MEMBER
    // =========================================
    public boolean updateMember(MemberDTO dto) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            // Find existing member
            Member member = session.get(
                    Member.class,
                    dto.getMemberId()
            );

            if (member == null) {

                transaction.rollback();

                System.out.println("Member Not Found!");

                return false;
            }

            // Update member details
            member.setFullName(dto.getFullName());
            member.setEmail(dto.getEmail());
            member.setPhone(dto.getPhone());
            member.setAddress(dto.getAddress());

            // Hibernate automatically detects changes.
            // @PreUpdate automatically updates updatedAt.

            transaction.commit();

            System.out.println("Member Updated Successfully!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            System.err.println("Member Update Failed!");
            e.printStackTrace();

            return false;
        }
    }


    // =========================================
    // DELETE MEMBER
    // =========================================
    public boolean deleteMember(String memberId) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSession()) {

            transaction = session.beginTransaction();

            Member member = session.get(
                    Member.class,
                    memberId
            );

            if (member == null) {

                transaction.rollback();

                System.out.println("Member Not Found!");

                return false;
            }

            // Delete using Hibernate
            session.remove(member);

            transaction.commit();

            System.out.println("Member Deleted Successfully!");

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            System.err.println("Member Delete Failed!");
            e.printStackTrace();

            return false;
        }
    }


    // =========================================
    // GET TOTAL MEMBERS COUNT
    // =========================================
    public int getTotalMembersCount() {

        try (Session session = HibernateUtil.getSession()) {

            Long count = session.createQuery(
                    "SELECT COUNT(m) FROM Member m",
                    Long.class
            ).getSingleResult();

            return count.intValue();

        } catch (Exception e) {

            System.err.println("Get Member Count Failed!");
            e.printStackTrace();

            return 0;
        }
    }
}