import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

class ProductCRUD {
    private List<Product> products;

    public ProductCRUD() {
        this.products = loadProducts();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(int productId, String productName, double price) {
    if (!isProductIdExists(productId)) {
        Product newProduct = new Product(productId, productName, price);
        products.add(newProduct);
        saveProducts();
        JOptionPane.showMessageDialog(null, "Product added successfully!");
    } else {
        JOptionPane.showMessageDialog(null, "Product ID already exists. Please choose a different ID.");
    }
}

private boolean isProductIdExists(int productId) {
    for (Product product : products) {
        if (product.getProductId() == productId) {
            return true;
        }
    }
    return false;
}
    public void editProductName(int productId, String newProductName) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setProductName(newProductName);
            saveProducts();
        }
    }

    public void deleteProduct(int productId) {
        Product product = getProductById(productId);
        if (product != null) {
            products.remove(product);
            // Remove associated invoices for the deleted product
            // (Implementation required based on your specific logic)
            saveProducts();
        }
    }

    Product getProductById(int productId) {
        for (Product product : products) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null;
    }

    private List<Product> loadProducts() {
        List<Product> productList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int productId = Integer.parseInt(parts[0]);
                String productName = parts[1];
                double price = Double.parseDouble(parts[2]);
                Product product = new Product(productId, productName, price);
                productList.add(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return productList;
    }

    private void saveProducts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"))) {
            for (Product product : products) {
                writer.write(product.getProductId() + "," + product.getProductName() + "," + product.getPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    