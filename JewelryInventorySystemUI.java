import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;

/* =======================
   Jewelry Node Class
   This represents a single jewelry item.
   Each item has an id, name, type, price, quantity.
   It also has a "next" pointer for hash table chaining.
   ======================= */
class Jewelry {
    int id; // Unique ID for jewelry
    String name; // Name of jewelry
    String type; // Type like Ring, Necklace, etc.
    double price; // Price in currency
    int quantity; // Quantity available
    Jewelry next; // Next pointer for linked list chaining in hash table

    public Jewelry(int id, String name, String type, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }
}

/*
 * =======================
 * JewelryHashTable
 * Implements a hash table for storing jewelry items.
 * Uses separate chaining (linked list) for collision handling.
 * =======================
 */
class JewelryHashTable {

    private final int TABLE_SIZE = 10; // Size of hash table
    private Jewelry[] table = new Jewelry[TABLE_SIZE]; // Array to store chains

    // Simple hash function: key mod table size
    private int hashFunction(int key) {
        return key % TABLE_SIZE;
    }

    public Jewelry[] getTable() {
        return table;
    }

    /* ===== Add new item ===== */
    public void addItem(int id, String name, String type, double price, int quantity) {
        int index = hashFunction(id); // Compute index in table
        Jewelry newItem = new Jewelry(id, name, type, price, quantity);

        if (table[index] == null) { // If no item exists, insert directly
            table[index] = newItem;
        } else { // Collision: traverse to end and append
            Jewelry t = table[index];
            while (t.next != null)
                t = t.next;
            t.next = newItem;
        }
    }

    /* ===== Search item by ID ===== */
    public Jewelry searchItem(int id) {
        Jewelry t = table[hashFunction(id)];
        while (t != null) {
            if (t.id == id)
                return t; // Found the item
            t = t.next; // Move to next in chain
        }
        return null; // Not found
    }

    /* ===== Update existing item ===== */
    public boolean updateItem(int id, double price, int qty) {
        Jewelry j = searchItem(id);
        if (j == null)
            return false; // Item not found
        j.price = price;
        j.quantity = qty;
        return true;
    }

    /* ===== Delete item by ID ===== */
    public boolean deleteItem(int id) {
        int idx = hashFunction(id);
        Jewelry t = table[idx], prev = null;

        while (t != null && t.id != id) { // Search for item in chain
            prev = t;
            t = t.next;
        }

        if (t == null)
            return false; // Item not found

        if (prev == null)
            table[idx] = t.next; // Item is head of chain
        else
            prev.next = t.next; // Bypass the node

        return true;
    }

    /* ===== Sell an item ===== */
    public String sellItem(int id, int qty) {
        Jewelry j = searchItem(id);
        if (j == null)
            return "Item not found"; // No such item
        if (j.quantity < qty)
            return "Not enough stock"; // Not enough quantity

        j.quantity -= qty; // Deduct sold quantity
        return "Sale successful. Remaining: " + j.quantity;
    }
}

/*
 * =======================
 * BackgroundPanel
 * Custom JPanel to paint a background image.
 * =======================
 */
class BackgroundPanel extends JPanel {

    private Image bg;

    public BackgroundPanel(String imagePath) {
        try {
            // Load image from resources
            bg = ImageIO.read(getClass().getResource(imagePath));
        } catch (Exception e) {
            bg = null; // If image not found, leave blank
        }
        setOpaque(false); // Make panel transparent for custom painting
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this); // Draw full-size
        }
    }
}

/*
 * =======================
 * JewelryInventorySystemUI
 * Main UI class for the jewelry inventory system
 * =======================
 */
public class JewelryInventorySystemUI extends JFrame {

    private JewelryHashTable inventory = new JewelryHashTable(); // Our hash table
    private JTextField idField, nameField, typeField, priceField, quantityField; // Form fields
    private InventoryTableWindow inventoryWindow = null; // Singleton inventory
    // window
    boolean CARD_VIEW = true; // Toggle between card view and table view for inventory
    // Luxury colors
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color GLASS = new Color(255, 255, 255, 40);

    public JewelryInventorySystemUI() {

        setTitle("Jewelry Inventory System");
        setSize(1280, 720);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set background panel with image
        setContentPane(new BackgroundPanel("/images/bg.jpg"));
        setLayout(new BorderLayout());

        /* ===== HEADER ===== */
        JLabel title = new JLabel("💎 Jewelry Inventory System", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(GOLD);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        /* ===== RIGHT SIDE PANEL ===== */
        JPanel rightSide = new JPanel();
        rightSide.setOpaque(false);
        rightSide.setLayout(new BorderLayout(10, 10));
        rightSide.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 30));

        /* ===== FORM PANEL ===== */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GOLD),
                "Jewelry Details",
                0, 0,
                new Font("Serif", Font.BOLD, 16),
                GOLD));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8); // spacing between form elements

        // Create text fields
        idField = createField();
        nameField = createField();
        typeField = createField();
        priceField = createField();
        quantityField = createField();

        // Add labels + fields to form panel
        addField(formPanel, g, 0, "ID:", idField);
        addField(formPanel, g, 1, "Name:", nameField);
        addField(formPanel, g, 2, "Type:", typeField);
        addField(formPanel, g, 3, "Price:", priceField);
        addField(formPanel, g, 4, "Quantity:", quantityField);

        rightSide.add(formPanel, BorderLayout.NORTH);

        /* ===== BUTTON GRID ===== */
        JPanel btnGrid = new JPanel(new GridLayout(3, 2, 12, 12));
        btnGrid.setOpaque(false);
        btnGrid.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addBtn = createButton("Add");
        JButton searchBtn = createButton("Search");
        JButton updateBtn = createButton("Update");
        JButton deleteBtn = createButton("Delete");
        JButton sellBtn = createButton("Sell");
        JButton showBtn = createButton("Inventory");

        // Connect buttons to actions
        addBtn.addActionListener(e -> addItem());
        searchBtn.addActionListener(e -> searchItem());
        updateBtn.addActionListener(e -> updateItem());
        deleteBtn.addActionListener(e -> deleteItem());
        sellBtn.addActionListener(e -> sellItem());
        showBtn.addActionListener(e -> showInventoryWindow()); // Singleton window

        // Add buttons to grid
        btnGrid.add(addBtn);
        btnGrid.add(searchBtn);
        btnGrid.add(updateBtn);
        btnGrid.add(deleteBtn);
        btnGrid.add(sellBtn);
        btnGrid.add(showBtn);

        rightSide.add(btnGrid, BorderLayout.CENTER);
        add(rightSide, BorderLayout.EAST);

        loadDefaultItems(); // Load some default jewelry
    }

    /* ===== Creates luxury glass-style text field ===== */
    private JTextField createField() {
        JTextField f = new JTextField() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(GLASS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18); // background
                g2.setColor(GOLD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18); // border
                g2.dispose();
                super.paintComponent(g);
            }
        };

        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setOpaque(false);
        f.setForeground(Color.WHITE);
        f.setCaretColor(GOLD);
        f.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        f.setPreferredSize(new Dimension(240, 38));
        return f;
    }

    /* ===== Helper method to add label + field to form panel ===== */
    private void addField(JPanel p, GridBagConstraints g, int y, String label, JTextField field) {
        g.gridx = 0;
        g.gridy = y;
        g.anchor = GridBagConstraints.LINE_END;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(GOLD);
        p.add(l, g);

        g.gridx = 1;
        g.anchor = GridBagConstraints.LINE_START;
        p.add(field, g);
    }

    /* ===== Show a luxury custom dialog ===== */
    private void showCustomDialog(String message) {
        // Create modal dialog without title bar
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);

        // Make the dialog itself transparent
        dialog.setBackground(new Color(0, 0, 0, 0));

        // Rounded panel
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 40, 40, 220)); // semi-transparent rounded background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        panel.setLayout(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // padding

        // Message
        JLabel msg = new JLabel(message, JLabel.CENTER);
        msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msg.setForeground(GOLD);
        panel.add(msg, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton ok = new JButton("OK");
        ok.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ok.setForeground(GOLD);
        ok.setBackground(new Color(40, 40, 40, 180));
        ok.setFocusPainted(false);
        ok.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // padding inside button
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);

        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    /* ===== Luxury Rounded Button ===== */
    private JButton createButton(String text) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(GOLD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(GOLD);
        b.setBackground(new Color(40, 40, 40));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    /* ===== Load default jewelry items ===== */
    private void loadDefaultItems() {

        inventory.addItem(101, "Gold Ring", "Ring", 25000, 10);
        inventory.addItem(102, "Diamond Necklace", "Necklace", 120000, 5);
        inventory.addItem(103, "Silver Bracelet", "Bracelet", 8000, 15);
        inventory.addItem(104, "Platinum Ring", "Ring", 55000, 7);
        inventory.addItem(105, "Pearl Earrings", "Earrings", 18000, 12);

        inventory.addItem(106, "Ruby Pendant", "Pendant", 45000, 6);
        inventory.addItem(107, "Emerald Ring", "Ring", 38000, 8);
        inventory.addItem(108, "Gold Chain", "Chain", 30000, 14);
        inventory.addItem(109, "Diamond Stud Earrings", "Earrings", 60000, 9);
        inventory.addItem(110, "Silver Anklet", "Anklet", 5000, 20);

        inventory.addItem(111, "Sapphire Necklace", "Necklace", 95000, 4);
        inventory.addItem(112, "Rose Gold Bracelet", "Bracelet", 27000, 11);
        inventory.addItem(113, "Diamond Bangle", "Bangle", 85000, 6);
        inventory.addItem(114, "Gold Hoop Earrings", "Earrings", 22000, 13);
        inventory.addItem(115, "Platinum Chain", "Chain", 65000, 5);

        inventory.addItem(116, "Pearl Necklace", "Necklace", 42000, 7);
        inventory.addItem(117, "Silver Ring", "Ring", 4000, 18);
        inventory.addItem(118, "Diamond Bracelet", "Bracelet", 98000, 3);
        inventory.addItem(119, "Ruby Earrings", "Earrings", 36000, 10);
        inventory.addItem(120, "Gold Bangle", "Bangle", 48000, 9);

        inventory.addItem(121, "Emerald Pendant", "Pendant", 33000, 8);
        inventory.addItem(122, "Silver Chain", "Chain", 6000, 16);
        inventory.addItem(123, "Platinum Bracelet", "Bracelet", 72000, 4);
        inventory.addItem(124, "Diamond Ring", "Ring", 110000, 6);
        inventory.addItem(125, "Pearl Bracelet", "Bracelet", 21000, 12);
    }

    /* ===== CRUD + Sell Operations ===== */
    private void addItem() {
        try {
            inventory.addItem(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(),
                    typeField.getText(),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(quantityField.getText()));
            showCustomDialog("Item added successfully");
        } catch (Exception e) {
            showCustomDialog("Invalid input");
        }
    }

    private void searchItem() {
        try {
            Jewelry j = inventory.searchItem(Integer.parseInt(idField.getText()));
            if (j != null) {
                nameField.setText(j.name);
                typeField.setText(j.type);
                priceField.setText("" + j.price);
                quantityField.setText("" + j.quantity);
            } else
                showCustomDialog("Item not found");
        } catch (Exception e) {
            showCustomDialog("Invalid ID");
        }
    }

    private void updateItem() {
        try {
            boolean success = inventory.updateItem(
                    Integer.parseInt(idField.getText()),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(quantityField.getText()));
            if (success)
                showCustomDialog("Item updated successfully");
            else
                showCustomDialog("Item not found");
        } catch (Exception e) {
            showCustomDialog("Invalid input");
        }
    }

    private void deleteItem() {
        try {
            boolean success = inventory.deleteItem(Integer.parseInt(idField.getText()));
            if (success)
                showCustomDialog("Item deleted successfully");
            else
                showCustomDialog("Item not found");
        } catch (Exception e) {
            showCustomDialog("Invalid ID");
        }
    }

    private void sellItem() {
        try {
            String result = inventory.sellItem(
                    Integer.parseInt(idField.getText()),
                    Integer.parseInt(quantityField.getText()));
            showCustomDialog(result);
        } catch (Exception e) {
            showCustomDialog("Invalid input");
        }
    }

    /* ===== Singleton Inventory Window ===== */
    private void showInventoryWindow() {

        if (CARD_VIEW) {
            new InventoryCardWindow(inventory, this).setVisible(true);
        } else {
            if (inventoryWindow == null)
                inventoryWindow = new InventoryTableWindow(inventory, this);
            inventoryWindow.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JewelryInventorySystemUI().setVisible(true));
    }
}