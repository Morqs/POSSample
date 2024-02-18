class Invoice {
    int invoiceId;
    int productId;
    int qty;
    int customerId;
    String customerName;

    public Invoice(int invoiceId, int productId, int qty, int customerId, String customerName) {
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.qty = qty;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public int getProductId() {
        return productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getQty() {
        return qty;
    }
}