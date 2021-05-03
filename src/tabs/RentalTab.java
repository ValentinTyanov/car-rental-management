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
public class RentalTab extends JPanel {
  Connection conn;
  PreparedStatement state;
  ResultSet result;
  int rentalId = 1;

  JPanel rentalPanel = new JPanel();

  JPanel upPanel = new JPanel();
  JPanel midPanel = new JPanel();
  JPanel downPanel = new JPanel();

  JLabel clientLabel = new JLabel("Client ID");
  JLabel carLabel = new JLabel("Car ID");
  JLabel durationLabel = new JLabel("Rental Duration");

  JTextField clientField = new JTextField();
  JTextField carField = new JTextField();
  JTextField durationField = new JTextField();

  JButton addButton = new JButton("Add");
  JButton deleteButton = new JButton("Delete");
  JButton editButton = new JButton("Edit");
  JButton searchButton = new JButton("Search by minimal duration");
  JButton refreshButton = new JButton("Refresh");

  JTable table = new JTable();
  JScrollPane mouseScroll = new JScrollPane(table);

  public RentalTab() throws Exception {
    upPanel.setLayout(new GridLayout(5, 2));
    upPanel.add(clientLabel);
    upPanel.add(clientField);
    upPanel.add(carLabel);
    upPanel.add(carField);
    upPanel.add(durationLabel);
    upPanel.add(durationField);

    midPanel.setLayout(new GridLayout(3, 2));
    midPanel.add(addButton);
    midPanel.add(searchButton);
    midPanel.add(editButton);
    midPanel.add(deleteButton);
    midPanel.add(refreshButton);

    mouseScroll.setPreferredSize(new Dimension(600, 200));
    downPanel.add(mouseScroll);

    rentalPanel.setLayout(new GridLayout(3, 1));
    rentalPanel.add(upPanel);
    rentalPanel.add(midPanel);
    rentalPanel.add(downPanel);
    this.add(rentalPanel);

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
      int carId = Integer.parseInt(carField.getText().trim());
      int clientId = Integer.parseInt(clientField.getText().trim());
      int duration = Integer.parseInt(durationField.getText().trim());
      double totalPrice = 0;

      String dailyPriceSql = "SELECT DISTINCT price_per_day FROM cars WHERE car_id = ?";
      String insertSql = "INSERT into rentals values(null, ?, ?, ?, ?)";

      try {
        conn = DBConnection.getConnection();
        totalPrice = priceCalculation(dailyPriceSql, "Add", duration, carId);

        state = conn.prepareStatement(insertSql);
        state.setInt(1, carId);
        state.setInt(2, clientId);
        state.setDouble(3, totalPrice);
        state.setInt(4, duration);

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
      int duration = Integer.parseInt(durationField.getText().trim());
      double totalPrice = 0;

      String sqlOne = "SELECT DISTINCT duration, rental_price FROM rentals WHERE rental_id = ?";
      String sqlTwo = "UPDATE rentals set duration = ?, rental_price = ? WHERE rental_id = ?";

      try {
        conn = DBConnection.getConnection();
        totalPrice = priceCalculation(sqlOne, "Edit", duration, rentalId);

        state = conn.prepareStatement(sqlTwo);
        state.setInt(1, duration);
        state.setDouble(2, totalPrice);
        state.setInt(3, rentalId);
        state.execute();

        refresh(table);
        rentalId = 1;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class DeleteAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();
      String sql = "DELETE FROM rentals WHERE rental_id = ?";

      try {
        state = conn.prepareStatement(sql);
        state.setInt(1, rentalId);
        state.execute();
        refresh(table);
        rentalId = 1;
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  class SearchAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      conn = DBConnection.getConnection();

      String sql =
          "SELECT DISTINCT r.rental_id, cl.name, cl.surname, c.brand, c.model, r.duration, r.rental_price"
              + " FROM rentals r"
              + " INNER JOIN clients cl ON r.client_id = cl.client_id"
              + " INNER JOIN cars c ON r.car_id = c.car_id"
              + " WHERE duration >= ?";
      try {
        state = conn.prepareStatement(sql);
        state.setString(1, durationField.getText());
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
      rentalId = Integer.parseInt(table.getValueAt(row, 0).toString());
      if (e.getClickCount() > 1) {
        carField.setText(table.getValueAt(row, 1).toString());
        clientField.setText(table.getValueAt(row, 2).toString());
        durationField.setText(table.getValueAt(row, 5).toString());

        clientField.setEnabled(false);
        carField.setEnabled(false);
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

    String sql =
        "SELECT DISTINCT r.rental_id, cl.name, cl.surname, c.brand, c.model, r.duration, r.rental_price"
            + " FROM rentals r"
            + " INNER JOIN clients cl ON r.client_id = cl.client_id"
            + " INNER JOIN cars c ON r.car_id = c.car_id;";

    state = conn.prepareStatement(sql);
    result = state.executeQuery();
    table.setModel(new MyModel(result));
  }

  public void clearForm() {
    Set<JTextField> input = Set.of(clientField, carField, durationField);
    for (JTextField field : input) {
      field.setText(null);
    }
  }

  public void refresh(JTable table) throws Exception {
    refreshTable(table);
    clearForm();
    clientField.setEnabled(true);
    carField.setEnabled(true);
  }

  public double priceCalculation(String sql, String methodType, int duration, int id)
      throws Exception {
    state = conn.prepareStatement(sql);
    state.setInt(1, id);
    result = state.executeQuery();
    JTable tempTable = new JTable();
    tempTable.setModel(new MyModel(result));
    double dailyPrice = 0;

    if (methodType.equals("Edit")) {
      dailyPrice =
          Double.parseDouble(tempTable.getValueAt(0, 1).toString())
              / Double.parseDouble(tempTable.getValueAt(0, 0).toString());
    } else {
      dailyPrice = Double.parseDouble(tempTable.getValueAt(0, 0).toString());
    }
    return dailyPrice * duration;
  }
}
