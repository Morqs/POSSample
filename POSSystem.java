import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class POSSystem extends JFrame {
    private ProductCRUD productCRUD;
    private List<Invoice> invoices;

    public POSSystem() {
        productCRUD = new ProductCRUD();
        invoices = loadInvoices();
        initUI();
    }

    private void initUI() {
        setTitle("POS System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3, 10, 10));

        JButton addProductBtn = createStyledButton("Add Product");
        JButton viewProductsBtn = createStyledButton("View Products");
        JButton addInvoiceBtn = createStyledButton("Add Invoice");
        JButton viewCustomerInvoiceBtn = createStyledButton("View Customer Invoice");
        JButton editProductNameBtn = createStyledButton("Edit Product Name");
        JButton deleteProductBtn = createStyledButton("Delete Product");

        addProductBtn.addActionListener(e -> addProduct());
        viewProductsBtn.addActionListener(e -> viewProducts());
        addInvoiceBtn.addActionListener(e -> addInvoice());
        viewCustomerInvoiceBtn.addActionListener(e -> viewCustomerInvoice());
        editProductNameBtn.addActionListener(e -> editProductName());
        deleteProductBtn.addActionListener(e -> deleteProduct());

        buttonPanel.add(addProductBtn);
        buttonPanel.add(viewProductsBtn);
        buttonPanel.add(addInvoiceBtn);
        buttonPanel.add(viewCustomerInvoiceBtn);
        buttonPanel.add(editProductNameBtn);
        buttonPanel.add(deleteProductBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(60, 141, 188));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void addProduct() {
        // Create a dialog to get product information
        JDialog addProductDialog = new JDialog(this, "Add Product", true);
        addProductDialog.setSize(300, 200);
        addProductDialog.setLayout(new GridLayout(4, 2));
    
        JTextField productIdField = new JTextField();
        JTextField productNameField = new JTextField();
        JTextField priceField = new JTextField();
        JButton addButton = new JButton("Add Product");
    
        addProductDialog.add(new JLabel(" Product ID:"));
        addProductDialog.add(productIdField);
        addProductDialog.add(new JLabel(" Product Name:"));
        addProductDialog.add(productNameField);
        addProductDialog.add(new JLabel(" Product Price:"));
        addProductDialog.add(priceField);
        addProductDialog.add(new JLabel(""));
        addProductDialog.add(addButton);
    
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int productId = Integer.parseInt(productIdField.getText());
                    String productName = productNameField.getText();
                    double price = Double.parseDouble(priceField.getText());
    
                    productCRUD.addProduct(productId, productName, price);
                    addProductDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(POSSystem.this, "Invalid input. Please enter valid numeric values.");
                }
            }
        });
    
        // Set the location of the dialog relative to the main frame
        addProductDialog.setLocationRelativeTo(this);
    
        addProductDialog.setVisible(true);
    }
    

    private int getSoldQuantity(int productId) {
        int soldQty = 0;

        for (Invoice invoice : invoices) {
            if (invoice.getProductId() == productId) {
                soldQty += invoice.getQty();
            }
        }

        return soldQty;
    }

    private void viewProducts() {
        // Create a dialog to display the products
        JDialog viewProductsDialog = new JDialog(this, "View Products", true);
        viewProductsDialog.setSize(400, 300);
        viewProductsDialog.setLayout(new BorderLayout());
    
        JTextArea productsTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(productsTextArea);
    
        List<Product> products = productCRUD.getProducts();
        StringBuilder productsInfo = new StringBuilder("Products List:\n");
    
        for (Product product : products) {
            int soldQty = getSoldQuantity(product.getProductId());
            productsInfo.append(product.getProductId())
                    .append(". ")
                    .append(product.getProductName())
                    .append(" - Price: $")
                    .append(product.getPrice())
                    .append(" - Sold: ")
                    .append(soldQty)
                    .append(" times\n");
        }
    
        productsTextArea.setText(productsInfo.toString());  // Make sure to complete this line
    
        viewProductsDialog.add(scrollPane, BorderLayout.CENTER);
    
        // Set the location of the dialog relative to the main frame
        viewProductsDialog.setLocationRelativeTo(this);
    
        viewProductsDialog.setVisible(true);
    }
    
    private void addInvoice() {
        // Create a dialog to get invoice information
        JDialog addInvoiceDialog = new JDialog(this, "Add Invoice", true);
        addInvoiceDialog.setSize(300, 200);
        addInvoiceDialog.setLayout(new GridLayout(5, 2));
    
        JTextField productIdField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField customerIdField = new JTextField();
        JTextField customerNameField = new JTextField();
        JButton addButton = new JButton("Add Invoice");
    
        addInvoiceDialog.add(new JLabel("Product ID:"));
        addInvoiceDialog.add(productIdField);
        addInvoiceDialog.add(new JLabel("Quantity:"));
        addInvoiceDialog.add(qtyField);
        addInvoiceDialog.add(new JLabel("Customer ID:"));
        addInvoiceDialog.add(customerIdField);
        addInvoiceDialog.add(new JLabel("Customer Name:"));
        addInvoiceDialog.add(customerNameField);
        addInvoiceDialog.add(new JLabel(""));
        addInvoiceDialog.add(addButton);
    
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int productId = Integer.parseInt(productIdField.getText());
                    int qty = Integer.parseInt(qtyField.getText());
                    int customerId = Integer.parseInt(customerIdField.getText());
                    String customerName = customerNameField.getText();
    
                    Product product = productCRUD.getProductById(productId);
    
                    if (product != null) {
                        int invoiceId = invoices.size() + 1;
                        Invoice newInvoice = new Invoice(invoiceId, productId, qty, customerId, customerName);
                        invoices.add(newInvoice);
    
                        saveInvoices();
                        addInvoiceDialog.dispose();
                        JOptionPane.showMessageDialog(POSSystem.this, "Invoice added successfully! Total amount: $" + (product.getPrice() * qty));
                    } else {
                        JOptionPane.showMessageDialog(POSSystem.this, "Product not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(POSSystem.this, "Invalid input. Please enter valid numeric values.");
                }
            }
        });
    
        // Set the location of the dialog relative to the main frame
        addInvoiceDialog.setLocationRelativeTo(this);
    
        addInvoiceDialog.setVisible(true);
    }
    
    private void viewCustomerInvoice() {
        // Create a dialog to get customer ID
        JDialog customerInvoiceDialog = new JDialog(this, "View Customer Invoice", true);
        customerInvoiceDialog.setSize(300, 100);
        customerInvoiceDialog.setLayout(new GridLayout(2, 2));
    
        JTextField customerIdField = new JTextField();
        JButton viewButton = new JButton("View Invoice");
    
        customerInvoiceDialog.add(new JLabel("Customer ID:"));
        customerInvoiceDialog.add(customerIdField);
        customerInvoiceDialog.add(new JLabel(""));
        customerInvoiceDialog.add(viewButton);
    
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int customerId = Integer.parseInt(customerIdField.getText());
                    String customerInvoiceInfo = "Customer Invoice for ID " + customerId + ":\n";
    
                    double totalAmount = 0;
    
                    for (Invoice invoice : invoices) {
                        if (invoice.getCustomerId() == customerId) {
                            Product product = productCRUD.getProductById(invoice.getProductId());
                            if (product != null) {
                                customerInvoiceInfo += product.getProductName() + " - Qty: " + invoice.getQty() + " - Amount: $" + (product.getPrice() * invoice.getQty()) + "\n";
                                totalAmount += product.getPrice() * invoice.getQty();
                            }
                        }
                    }
    
                    customerInvoiceInfo += "Total Amount: $" + totalAmount;
    
                    JOptionPane.showMessageDialog(POSSystem.this, customerInvoiceInfo);
                    customerInvoiceDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(POSSystem.this, "Invalid input. Please enter a valid numeric value.");
                }
            }
        });
    
        // Set the location of the dialog relative to the main frame
        customerInvoiceDialog.setLocationRelativeTo(this);
    
        customerInvoiceDialog.setVisible(true);
    }
    
    private void editProductName() {
        // Create a dialog to get product ID and new product name
        JDialog editProductNameDialog = new JDialog(this, "Edit Product Name", true);
        editProductNameDialog.setSize(300, 150);
        editProductNameDialog.setLayout(new GridLayout(3, 2));
    
        JTextField productIdField = new JTextField();
        JTextField newProductNameField = new JTextField();
        JButton editButton = new JButton("Edit Product Name");
    
        editProductNameDialog.add(new JLabel("Product ID to edit:"));
        editProductNameDialog.add(productIdField);
        editProductNameDialog.add(new JLabel("New Product Name:"));
        editProductNameDialog.add(newProductNameField);
        editProductNameDialog.add(new JLabel(""));
        editProductNameDialog.add(editButton);
    
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int productId = Integer.parseInt(productIdField.getText());
                    String newProductName = newProductNameField.getText();
    
                    productCRUD.editProductName(productId, newProductName);
                    editProductNameDialog.dispose();
                    JOptionPane.showMessageDialog(POSSystem.this, "Product name updated successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(POSSystem.this, "Invalid input. Please enter a valid numeric value for product ID.");
                }
            }
        });
    
        // Set the location of the dialog relative to the main frame
        editProductNameDialog.setLocationRelativeTo(this);
    
        editProductNameDialog.setVisible(true);
    }
    
    private void deleteProduct() {
        // Create a dialog to get product ID
        JDialog deleteProductDialog = new JDialog(this, "Delete Product", true);
        deleteProductDialog.setSize(300, 100);
        deleteProductDialog.setLayout(new GridLayout(2, 2));
    
        JTextField productIdField = new JTextField();
        JButton deleteButton = new JButton("Delete Product");
    
        deleteProductDialog.add(new JLabel("Product ID to delete:"));
        deleteProductDialog.add(productIdField);
        deleteProductDialog.add(new JLabel(""));
        deleteProductDialog.add(deleteButton);
    
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int productId = Integer.parseInt(productIdField.getText());
    
                    productCRUD.deleteProduct(productId);
                    deleteProductDialog.dispose();
                    JOptionPane.showMessageDialog(POSSystem.this, "Product deleted successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(POSSystem.this, "Invalid input. Please enter a valid numeric value for product ID.");
                }
            }
        });
    
        // Set the location of the dialog relative to the main frame
        deleteProductDialog.setLocationRelativeTo(this);
    
        deleteProductDialog.setVisible(true);
    }

    private List<Invoice> loadInvoices() {
        List<Invoice> invoiceList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("invoices.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int invoiceId = Integer.parseInt(parts[0]);
                int productId = Integer.parseInt(parts[1]);
                int qty = Integer.parseInt(parts[2]);
                int customerId = Integer.parseInt(parts[3]);
                String customerName = parts[4];
                Invoice invoice = new Invoice(invoiceId, productId, qty, customerId, customerName);
                invoiceList.add(invoice);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return invoiceList;
    }

    private void saveInvoices() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("invoices.txt"))) {
            for (Invoice invoice : invoices) {
                writer.write(
                        invoice.invoiceId + "," +
                                invoice.productId + "," +
                                invoice.qty + "," +
                                invoice.customerId + "," +
                                invoice.customerName
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                POSSystem posSystem = new POSSystem();
                posSystem.setVisible(true);
            }
        });
    }
}