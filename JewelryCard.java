import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;

public class JewelryCard extends JPanel {

    private static final Color GOLD = new Color(212, 175, 55);

    private JButton createButton(String text) {

        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

                g2.setColor(GOLD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(new Color(212, 175, 55));
        b.setBackground(new Color(40, 40, 40));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        return b;
    }

    public JewelryCard(Jewelry item, JewelryHashTable inventory, InventoryCardWindow parent) {

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 280));
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20, new Color(212, 175, 55)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // ===== IMAGE =====
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        try {

            String imagePath = "/images/" + item.name.trim() + ".jpg";

            Image img = ImageIO.read(getClass().getResource(imagePath));

            img = img.getScaledInstance(180, 140, Image.SCALE_SMOOTH);

            imageLabel.setIcon(new ImageIcon(img));

        } catch (Exception e) {

            imageLabel.setText("No Image");
            imageLabel.setForeground(Color.WHITE);

        }

        add(imageLabel, BorderLayout.NORTH);

        // ===== INFO =====
        JPanel info = new JPanel();
        info.setLayout(new GridLayout(4, 1));
        info.setOpaque(false);

        JLabel name = new JLabel(item.name, JLabel.CENTER);
        name.setForeground(GOLD);
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel type = new JLabel("Type: " + item.type, JLabel.CENTER);
        JLabel price = new JLabel("₹ " + item.price, JLabel.CENTER);
        JLabel qty = new JLabel("Stock: " + item.quantity, JLabel.CENTER);

        type.setForeground(Color.WHITE);
        price.setForeground(Color.WHITE);
        qty.setForeground(Color.WHITE);

        info.add(name);
        info.add(type);
        info.add(price);
        info.add(qty);

        add(info, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);

        JButton sellBtn = createButton("Sell");
        JButton deleteBtn = createButton("Delete");

        sellBtn.addActionListener(e -> {
            inventory.sellItem(item.id, 1);
            parent.refreshCards();
        });

        deleteBtn.addActionListener(e -> {
            inventory.deleteItem(item.id);
            parent.refreshCards();
        });

        buttons.add(sellBtn);
        buttons.add(deleteBtn);

        add(buttons, BorderLayout.SOUTH);
    }
}