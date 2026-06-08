import javax.swing.*;
import java.awt.*;

public class InventoryCardWindow extends JFrame {

    private JewelryHashTable inventory;
    private JPanel cardPanel;

    public InventoryCardWindow(JewelryHashTable inventory, JFrame parent) {

        this.inventory = inventory;

        setTitle("Jewelry Inventory Cards");
        setSize(1280, 720);
        setLocationRelativeTo(parent);

        setContentPane(new BackgroundPanel("/images/INVbg.jpg"));
        setLayout(new BorderLayout());

        JLabel title = new JLabel("💎 Jewelry Inventory", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(new Color(222, 78, 11));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // Card container
        cardPanel = new JPanel();
        cardPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cardPanel.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        wrapper.add(cardPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setViewportBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {

            protected void configureScrollBarColors() {
                this.thumbColor = new Color(212, 175, 55);
                this.trackColor = new Color(40, 40, 40, 120);
            }

            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        refreshCards();

        SwingUtilities.invokeLater(() -> {
            cardPanel.revalidate();
            cardPanel.repaint();
        });
    }

    public void refreshCards() {

        cardPanel.removeAll();

        java.util.List<Jewelry> list = new java.util.ArrayList<>();

        for (Jewelry j : inventory.getTable()) {
            while (j != null) {
                list.add(j);
                j = j.next;
            }
        }

        // sort by ID
        list.sort((a, b) -> Integer.compare(a.id, b.id));

        for (Jewelry j : list) {
            cardPanel.add(new JewelryCard(j, inventory, this));
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }
}