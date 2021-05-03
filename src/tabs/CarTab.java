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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CarTab extends JPanel {
  Connection conn;
  PreparedStatement state;
  ResultSet result;
  int carId = 1;

  JPanel carPanel = new JPanel();

  JPanel upPanel = new JPanel();
  JPanel midPanel = new JPanel();
  JPanel downPanel = new JPanel();

  JLabel brandLabel = new JLabel("Brand");
  JLabel modelLabel = new JLabel("Model");
  JLabel colorLabel = new JLabel("Color");
  JLabel priceLabel = new JLabel("Price Per Day");

  JTextField brandField = new JTextField();
  JTextField modelField = new JTextField();
  JTextField colorField = new JTextField();
  JTextField priceField = new JTextField();

  JButton addButton = new JButton("Add");
  JButton deleteButton = new JButton("Delete");
  JButton editButton = new JButton("Edit");
  JButton searchButton = new JButton("Search by brand");
  JButton refreshButton = new JButton("Refresh");

  JTable table = new JTable();
  JScrollPane mouseScroll = new JScrollPane(table);

  public CarTab() throws Exception {
    upPanel.setLayout(new GridLayout(5, 2));
    upPanel.add(brandLabel);
    upPanel.add(brandField);
    upPanel.add(modelLabel);
    upPanel.add(modelField);
    upPanel.add(colorLabel);
    upPanel.add(colorField);
    upPanel.add(priceLabel);
    upPanel.add(priceField);

    midPanel.setLayout(new GridLayout(3, 2));
    midPanel.add(addButton);
    midPanel.add(searchButton);
    midPanel.add(editButton);
    midPanel.add(deleteButton);
    midPanel.add(refreshButton);

    mouseScroll.setPreferredSize(new Dimension(600, 200));
    downPanel.add(mouseScroll);

    carPanel.setLayout(new GridLayout(3, 1));
    carPanel.add(upPanel);
    carPanel.add(midPanel);
    carPanel.add(downPanel);
    this.add(carPanel);

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
      String sql = "INSERT into cars values(null, ?, ?, ?, ?)";
      try {
        state = conn.prepareStatement(sql);
        state.setString(1, brandField.getText());
        state.setString(2, modelField.getText());
        state.setString(3, colorField.getText());
        state.setDouble(4, Double.parseDouble(priceField.getText().trim()));

        state.execute();
        refreshTable(table);
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
          "UPDATE cars set brand = ?, model = ?, color = ?, price_per_day = ? WHERE car_id = ?";

      try {
        state = conn.prepareStatement(sql);
        state.setString(1, brandField.getText());
        state.setString(2, modelField.getText());
        state.setString(3, colorField.getText());
        state.setDouble(4, Double.parseDouble(priceField.getText().trim()));
        state.setInt(5, carId);

        state.execute();
        refreshTable(table);
        carId = 1;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class DeleteAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();
      String sql = "DELETE FROM cars WHERE car_id = ?";

      try {
        state = conn.prepareStatement(sql);
        state.setInt(1, carId);
        state.execute();
        refreshTable(table);
        carId = 1;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class SearchAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();
      String sql = "SELECT * FROM cars WHERE brand = ?";
      try {
        state = conn.prepareStatement(sql);
        state.setString(1, brandField.getText());
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
      carId = Integer.parseInt(table.getValueAt(row, 0).toString());
      if (e.getClickCount() > 1) {
        brandField.setText(table.getValueAt(row, 1).toString());
        modelField.setText(table.getValueAt(row, 2).toString());
        colorField.setText(table.getValueAt(row, 3).toString());
        priceField.setText(table.getValueAt(row, 4).toString());
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

    state = conn.prepareStatement("SELECT * FROM cars");
    result = state.executeQuery();
    table.setModel(new MyModel(result));
  }

  public void clearForm() {
    Set<JTextField> input = Set.of(brandField, modelField, colorField, priceField);
    for (JTextField field : input) {
      field.setText(null);
    }
  }

  public void refresh(JTable table) throws Exception {
    refreshTable(table);
    clearForm();
  }
}
