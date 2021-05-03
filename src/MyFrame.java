import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import tabs.CarTab;
import tabs.ClientTab;
import tabs.RentalTab;

@SuppressWarnings("serial")
public class MyFrame extends JFrame {

  JTabbedPane tab = new JTabbedPane();
  ClientTab clientTab = new ClientTab();
  CarTab carTab = new CarTab();
  RentalTab rentalTab = new RentalTab();

  public MyFrame() throws Exception {
    this.setSize(650, 700);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setVisible(true);

    tab.add("Clients", clientTab);
    tab.add("Cars", carTab);
    tab.add("Rental", rentalTab);
    this.add(tab);
  }
}
