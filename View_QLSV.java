/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class View_QLSV extends JFrame {

    private JTextField txt_Ma, txt_Ten, txt_Email, txt_SDT;
    private JTextArea txt_DiaChi;
    private JRadioButton rdo_Nam, rdo_Nu;
    private ButtonGroup buttonGroup1;
    private JTable tbl_QLSV;
    private DefaultTableModel mol;
   

    public View_QLSV() {
        // Thiết lập JFrame
        setTitle("Quản Lý Sinh Viên");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo các component
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        JLabel lblMa = new JLabel("Mã SV:");
        txt_Ma = new JTextField();
        JLabel lblTen = new JLabel("Họ tên:");
        txt_Ten = new JTextField();
        JLabel lblEmail = new JLabel("Email:");
        txt_Email = new JTextField();
        JLabel lblSDT = new JLabel("SĐT:");
        txt_SDT = new JTextField();
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        rdo_Nam = new JRadioButton("Nam");
        rdo_Nu = new JRadioButton("Nữ");
        buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rdo_Nam);
        buttonGroup1.add(rdo_Nu);
        
        // Thiết lập bảng
        String[] columns = {"Mã SV", "Họ Tên", "Email", "SĐT", "Giới tính", "Địa chỉ"};
        mol = new DefaultTableModel(columns, 0);
        tbl_QLSV = new JTable(mol);
        JScrollPane scrollPane = new JScrollPane(tbl_QLSV);

        // Thiết lập các nút
        JButton btnThem = new JButton("Thêm");
        JButton btnXoa = new JButton("Xóa");
        JButton btnCapNhat = new JButton("Cập nhật");
        
        // Thêm các phần tử vào panel
        panel.add(lblMa);
        panel.add(txt_Ma);
        panel.add(lblTen);
        panel.add(txt_Ten);
        panel.add(lblEmail);
        panel.add(txt_Email);
        panel.add(lblSDT);
        panel.add(txt_SDT);
        panel.add(lblGioiTinh);
        JPanel genderPanel = new JPanel();
        genderPanel.add(rdo_Nam);
        genderPanel.add(rdo_Nu);
        panel.add(genderPanel);
        
        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        txt_DiaChi = new JTextArea(5, 20);
        JScrollPane diaChiScrollPane = new JScrollPane(txt_DiaChi);
        panel.add(lblDiaChi);
        panel.add(diaChiScrollPane);

        // Thêm các nút dưới bảng
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnCapNhat);

        // Layout chính
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện của nút Thêm
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Model_QLSV student = readForm();
                if (student != null) {
                    // Thêm sinh viên vào bảng
                    mol.addRow(student.toDataRow());
                    // Lưu sinh viên vào database
                    addStudentToDatabase(student);
                }
            }
        });

        // Xử lý sự kiện của nút Xóa
        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tbl_QLSV.getSelectedRow();
                if (selectedRow >= 0) {
                    String maSv = (String) tbl_QLSV.getValueAt(selectedRow, 0);
                    deleteStudentFromDatabase(maSv);
                    mol.removeRow(selectedRow); // Xóa khỏi bảng
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn một sinh viên để xóa!");
                }
            }
        });

        // Xử lý sự kiện của nút Cập nhật
        btnCapNhat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tbl_QLSV.getSelectedRow();
                if (selectedRow >= 0) {
                    Model_QLSV updatedStudent = readForm();
                    if (updatedStudent != null) {
                        for (int i = 0; i < 6; i++) {
                            tbl_QLSV.setValueAt(updatedStudent.toDataRow()[i], selectedRow, i);
                        }
                        updateStudentInDatabase(updatedStudent);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn một sinh viên để cập nhật!");
                }
            }
        });
    }

    // Đọc dữ liệu từ form và trả về đối tượng Model_QLSV
    private Model_QLSV readForm() {
        String ma = txt_Ma.getText().trim();
        String ten = txt_Ten.getText().trim();
        String email = txt_Email.getText().trim();
        String soDienThoai = txt_SDT.getText().trim();
        int gioiTinh = rdo_Nam.isSelected() ? 1 : 0;
        String diaChi = txt_DiaChi.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || email.isEmpty() || soDienThoai.isEmpty() || diaChi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
            return null;
        }

        return new Model_QLSV(ma, ten, email, soDienThoai, gioiTinh, diaChi);
    }

    // Thêm sinh viên vào database
    private void addStudentToDatabase(Model_QLSV student) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/qlsv", "root", "")) {
            String sql = "INSERT INTO sinhvien (ma, ten, email, sdt, gioitinh, diachi) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.getMa());
                stmt.setString(2, student.getTen());
                stmt.setString(3, student.getEmail());
                stmt.setString(4, student.getSoDienThoai());
                stmt.setInt(5, student.getGioiTinh());
                stmt.setString(6, student.getDiaChi());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật sinh viên trong database
    private void updateStudentInDatabase(Model_QLSV student) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/qlsv", "root", "")) {
            String sql = "UPDATE sinhvien SET ten = ?, email = ?, sdt = ?, gioitinh = ?, diachi = ? WHERE ma = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.getTen());
                stmt.setString(2, student.getEmail());
                stmt.setString(3, student.getSoDienThoai());
                stmt.setInt(4, student.getGioiTinh());
                stmt.setString(5, student.getDiaChi());
                stmt.setString(6, student.getMa());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa sinh viên khỏi database
    private void deleteStudentFromDatabase(String maSv) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/qlsv", "root", "")) {
            String sql = "DELETE FROM sinhvien WHERE ma = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maSv);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new View_QLSV().setVisible(true);
            }
        });
    }
}



