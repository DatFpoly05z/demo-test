/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demo;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author Chi Dat
 */
public class Repositories_QLSV1 {
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private String sql = null;
    public ArrayList<Model_QLSV> getAll(){
        sql = "select MASV,Hoten,Email,SoDT,Gioitinh,Diachi from STUDENTS";
        ArrayList<Model_QLSV> list_QLSV = new ArrayList<>();
        try {
            con = DBConnect.getConnection();
            ps = con.prepareStatement(sql);
            rs  = ps.executeQuery();
            while(rs.next()){
                String ma,ten,email,soDienThoai;
                int gioiTinh;
                String diaChi;
                ma = rs.getString(1);
                ten = rs.getString(2);
                email = rs.getString(3);
                soDienThoai = rs.getString(4);
                gioiTinh = rs.getInt(5);
                diaChi = rs.getString(6);
                Model_QLSV m = new Model_QLSV(ma, ten, email, soDienThoai, gioiTinh, diaChi);
                list_QLSV.add(m);
                
            }return list_QLSV;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
    public int them(Model_QLSV m){
        sql = "insert into STUDENTS(MASV,Hoten,Email,SoDT,Gioitinh,Diachi) values (?,?,?,?,?,?)";
        try {
            con = DBConnect.getConnection();
            ps = con.prepareStatement(sql);
            ps.setObject(1, m.getMa());
            ps.setObject(2, m.getTen());
            ps.setObject(3, m.getEmail());
            ps.setObject(4, m.getSoDienThoai());
            ps.setObject(5, m.getGioiTinh());
            ps.setObject(6, m.getDiaChi());
            return ps.executeUpdate();
      
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        
    }
    public int xoa(String ma){
        sql = "delete from STUDENTS where MASV like ?";
        try {
            con = DBConnect.getConnection();
            ps = con.prepareStatement(sql);
            ps.setObject(1, ma);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public int upDate(String ma,Model_QLSV m){
        sql = "update STUDENTS set MASV = ?,Hoten=?,Email=?,SoDT=?,Gioitinh=?,Diachi=? \n" +
"where MASV = ?";
        try {
            con = DBConnect.getConnection();
            ps = con.prepareStatement(sql);
            ps.setObject(1, m.getMa());
            ps.setObject(2, m.getTen());
            ps.setObject(3, m.getEmail());
            ps.setObject(4, m.getSoDienThoai());
            ps.setObject(5, m.getGioiTinh());
            ps.setObject(6, m.getDiaChi());
            ps.setObject(7, ma);
            return ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        }
    
        
    
    
}
