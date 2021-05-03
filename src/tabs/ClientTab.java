package tabs;

import config.DBConnection;
import config.MyModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ClientTab extends JPanel {
  Connection conn;
  PreparedStatement state;
  ResultSet result;
  int clientId = 1;

  JPanel clientPanel = new JPanel();

  JPanel upPanel = new JPanel();
  JPanel midPanel = new JPanel();
  JPanel downPanel = new JPanel();

  JLabel nameLabel = new JLabel("Name");
  JLabel surnameLabel = new JLabel("Surname");
  JLabel sexLabel = new JLabel("Sex");
  JLabel ageLabel = new JLabel("Age");
  JLabel salaryLabel = new JLabel("Salary");

  JTextField nameField = new JTextField();
  JTextField surnameField = new JTextField();
  JTextField ageField = new JTextField();
  JTextField salaryField = new JTextField();

  String[] genders = {"Male", "Female", "Other"};
  JComboBox<String> genderSelection = new JComboBox<>(genders);

  JButton addButton = new JButton("Add");
  JButton deleteButton = new JButton("Delete");
  JButton editButton = new JButton("Edit");
  JButton searchButton = new JButton("Search by name");
  JButton refreshButton = new JButton("Refresh");

  JTable table = new JTable();
  JScrollPane mouseScroll = new JScrollPane(table);

  public ClientTab() throws Exception {
    upPanel.setLayout(new GridLayout(5, 2));
    upPanel.add(nameLabel);
    upPanel.add(nameField);
    upPanel.add(surnameLabel);
    upPanel.add(surnameField);
    upPanel.add(sexLabel);
    upPanel.add(genderSelection);
    upPanel.add(ageLabel);
    upPanel.add(ageField);
    upPanel.add(salaryLabel);
    upPanel.add(salaryField);

    midPanel.setLayout(new GridLayout(3, 2));
    midPanel.add(addButton);
    midPanel.add(searchButton);
    midPanel.add(editButton);
    midPanel.add(deleteButton);
    midPanel.add(refreshButton);

    mouseScroll.setPreferredSize(new Dimension(600, 200));
    downPanel.add(mouseScroll);

    clientPanel.setLayout(new GridLayout(3, 1));
    clientPanel.add(upPanel);
    clientPanel.add(midPanel);
    clientPanel.add(downPanel);
    this.add(clientPanel);

    addButton.addActionListener(new AddAction());
    deleteButton.addActionListener(new DeleteAction());
    searchButton.addActionListener(new SearchAction());
    refreshButton.addActionListener(new RefreshAction());
    editButton.addActionListener(new EditAction());

    addButton.setBackground(new Color(102, 153, 153));
    deleteButton.setBackground(new Color(255, 0, 0));
    searchButton.setBackground(new Color(102, 153, 153));
    refreshButton.setBackground(new Color(102, 255, 102));
    editButton.setBackground(new Color(255, 153, 0));

    table.addMouseListener(new MouseAction());

    refreshTable(table);
  }

  class AddAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();
      String sql = "INSERT into clients values(null, ?, ?, ?, ?, ?)";
      try {
        state = conn.prepareStatement(sql);
        state.setString(1, nameField.getText().toUpperCase());
        state.setString(2, surnameField.getText().toUpperCase());
        state.setString(3, genderSelection.getSelectedItem().toString());
        state.setInt(4, Integer.parseInt(ageField.getText()));
        state.setDouble(5, Double.parseDouble(salaryField.getText()));

        state.execute();
        refresh(table);
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class EditAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();
      String sql =
          "UPDATE clients set name = ?, surname = ?, sex = ?, age = ?, salary = ? WHERE client_id = ?";

      try {
        state = conn.prepareStatement(sql);
        state.setString(1, nameField.getText());
        state.setString(2, surnameField.getText());
        state.setString(3, genderSelection.getSelectedItem().toString());
        state.setInt(4, Integer.parseInt(ageField.getText().trim()));
        state.setDouble(5, Double.parseDouble(salaryField.getText().trim()));
        state.setInt(6, clientId);

        state.execute();
        refresh(table);

        clientId = 1;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class DeleteAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();
      String sql = "DELETE FROM clients WHERE client_id = ?";

      try {
        state = conn.prepareStatement(sql);
        state.setInt(1, clientId);
        state.execute();
        refresh(table);
        clientId = 1;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class SearchAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();
      String sql = "SELECT * FROM clients WHERE name = ?";
      try {
        state = conn.prepareStatement(sql);
        state.setString(1, nameField.getText().toUpperCase());
        result = state.executeQuery();
        table.setModel(new MyModel(result));
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class RefreshAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        refresh(table);
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class MouseAction implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      int row = table.getSelectedRow();
      clientId = Integer.parseInt(table.getValueAt(row, 0).toString());
      if (e.getClickCount() > 1) {
        String gender = table.getValueAt(row, 3).toString();
        int index = !gender.endsWith("e") ? 2 : (gender.charAt(0) == 'm' ? 0 : 1);

        nameField.setText(table.getValueAt(row, 1).toString());
        surnameField.setText(table.getValueAt(row, 2).toString());
        genderSelection.setSelectedIndex(index);
        ageField.setText(table.getValueAt(row, 4).toString());
        salaryField.setText(table.getValueAt(row, 5).toString());
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
  }

  public void refreshTable(JTable table) throws Exception {
    conn = DBConnection.getConnection();

    state = conn.prepareStatement("SELECT * FROM clients");
    result = state.executeQuery();
    table.setModel(new MyModel(result));
  }

  public void clearForm() {
    Set<JTextField> input = Set.of(ageField, salaryField, nameField, surnameField);
    for (JTextField field : input) {
      field.setText(null);
    }
    genderSelection.setSelectedItem(genders[0]);
  }

  public void refresh(JTable table) throws Exception {
    refreshTable(table);
    clearForm();
  }
}
