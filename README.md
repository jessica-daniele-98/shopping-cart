## Shopping cart
The Shopping Cart project simulates the operation of a shopping cart for an online store.
It allows you to add, remove, and update products in the cart, calculate total prices, and interact with a series of REST endpoints to manage products and orders.

The goal is to provide a practical example of managing a shopping cart with Java and REST APIs.

### Technologies used
The project is developed with Spring Boot 4.0.2 and Java 25.
The database used is MongoDB.
Dependencies are managed using Maven.

### Collection structure
There are two collections, one representing the available products and the second representing the orders placed.

#### Product
```JAVA
    @Indexed(unique = true)
    String name;
    String description;
    double price;
    double vatRate;
    double priceWithVat;
```
where price and vatRate are entered by the user when creating the product, while priceWithVat is calculated:

$$
priceWithVat = (price * vatRate) + price
$$

The index on name ensures that no more products with the same name can be added. Furthermore, as it is the entry point for all product-related queries, it serves to optimize queries.

#### Order
```JAVA
    @Indexed(unique = true)
    String orderId;
    LocalDate createdAt;
    double totalPrice;
    double totalVat;
    double totalWithVat;
    List<OrderItem> products;
```
where OrderItem represents a list containing the products and the quantity ordered for that product.

The index on orderId ensures that each order always has a different ID. Furthermore, as it is the entry point for all queries relating to the order, it serves to optimize queries.

### Endpoint
All endpoints representing the CRUD operations of both collections: product, ```ProductController```, order, ```OrderController```

To add a product, use:
```java
@PostMapping("/products")
ProductDto addProduct(
        @RequestBody
        @Valid
        AddProductRequest request){}
```
where ```AddProductRequest(name, description, price, vatRate)``` 

To update a product:
```java
@PutMapping("/products/{name}")
void updateProduct(
        @PathVariable
        String name,
        @RequestBody
        @Valid
        ProductUpdateRequest request){}
```
where ```ProductUpdateRequest(price, vatRate, description)``` 

To add an order, use:
```java
@PostMapping("/orders")
OrderDto addOrder(
        @RequestBody
        @Valid
        AddRequest request){}
```
where ```AddRequest(products)```, products are a list containing an object with the product name and the quantity relating to the product

To update an order:
```java
@PutMapping("/orders/{id}")
void updateOrder(
    @PathVariable
    String id, 
    @RequestBody
    @Valid
    UpdateOrderRequest request){}
```
where ```UpdateOrderRequest(products)```, products are a list containing an object with the product name and the quantity relating to the product

### Run Test
To run the test command

```bash
cd scripts
chmod +x test.sh
./test.sh
```

### Run application
To run the application

```bash
cd scripts
chmod +x run.sh
./run.sh
```
The script simulates running the application by adding products, modifying them, viewing them, and deleting one.
It adds an order, modifies it, views it, and deletes it.
The script remains running for possible interactions with the application at ```localhost:8080``` until the ‘q’ key is pressed.

### Future improvements
Possible future developments include:
- User integration, i.e., login and authentication
- Endpoint security integration, so that only users with specific permissions can perform certain actions (e.g., only administrators can add or delete products)
- Integration of users within the order collection so that an order can be associated with the user who placed it
- Quantity of products in stock, integrating product availability checks during order creation
- Addition of discounts and promotions on orders and products

