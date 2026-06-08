import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class InventoryTableWindow extends JFrame {

    private JewelryHashTable inventory;
    private JTable table;
    private DefaultTableModel model;

    // Luxury colors
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color GLASS = new Color(255, 255, 255, 60);
    private static final Color DARK_BG = new Color(40, 40, 40, 180);

    public InventoryTableWindow(JewelryHashTable inventory, JFrame parent) {
        this.inventory = inventory;

        setTitle("📦 Inventory Dashboard");
        setSize(1280, 720);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Background Panel
        setContentPane(new BackgroundPanel("/images/bg.jpg"));
        setLayout(new BorderLayout(10, 10));

        // ===== HEADER =====
        JLabel title = new JLabel("📦 Current Inventory", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(GOLD);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = { "ID", "Name", "Type", "Price", "Quantity" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setOpaque(false);
        table.setFillsViewportHeight(true);
        table.setForeground(Color.BLACK);
        table.setGridColor(new Color(255, 255, 255, 40));

        // Cell renderer for luxury glass effect
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int col) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                if (isSelected) {
                    setBackground(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 120));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(GLASS);
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });

        // Table header style
        JTableHeader th = table.getTableHeader();
        th.setOpaque(true);
        th.setBackground(DARK_BG);
        th.setForeground(GOLD);
        th.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Apply luxury scrollbar
        scroll.getVerticalScrollBar().setUI(new LuxuryScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new LuxuryScrollBarUI());

        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));

        add(scroll, BorderLayout.CENTER);
        ;

        // ===== BUTTONS =====
        JButton refreshBtn = createButton("Refresh");
        JButton closeBtn = createButton("Close");

        refreshBtn.addActionListener(e -> refreshTable());
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(refreshBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Load table data
        refreshTable();
    }

    // Create luxury rounded button
    private JButton createButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(GOLD);
        b.setBackground(DARK_BG);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 38));
        return b;
    }

    // Refresh table data from inventory
    private void refreshTable() {

        model.setRowCount(0);

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
            model.addRow(new Object[] {
                    j.id, j.name, j.type, j.price, j.quantity
            });
        }
    }
}

class LuxuryScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color DARK = new Color(40, 40, 40, 180);

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = GOLD;
        this.trackColor = new Color(40, 40, 40, 120);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
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

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(GOLD);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(DARK);
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.dispose();
    }
}