import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ExpenseTracker {

    public static void main(String[] args) {
        try {

            // Connect to the MySQL database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "Swagat@8571");

            // Create a table to store expenses
            createExpenseTable(connection);

            System.out.println("Expense table is created successfully!!!!");

            // Start the application loop
            while (true) {
                displayMenu();
                Scanner scanner = new Scanner(System.in);
                int choice = scanner.nextInt();
                scanner.nextLine(); // newline

                switch (choice) {
                    case 1:
                        recordExpense(connection, scanner);
                        break;
                    case 2:
                        viewExpenses(connection);
                        break;
                    case 3:
                        System.out.println("THANK YOU");
                        connection.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); //prints the details of the exception to the standard error stream
        }
    }

    private static void createExpenseTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "date DATE," +
                "description VARCHAR(255)," +
                "amount DECIMAL(10, 2)" +
                ")";
        PreparedStatement statement = connection.prepareStatement(createTableSQL);
        statement.execute();
    }

    private static void displayMenu() {
        System.out.println("Expense Tracker Menu:");
        System.out.println("1. Record an Expense");
        System.out.println("2. View Expenses");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void recordExpense(Connection connection, Scanner scanner) throws SQLException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            System.out.print("Enter the expense date (dd-MM-yyyy): ");
            Date date = dateFormat.parse(scanner.nextLine());
            System.out.print("Enter a description: ");
            String description = scanner.nextLine();
            System.out.print("Enter the expense amount: ");
            double amount = scanner.nextDouble();

            String insertSQL = "INSERT INTO expenses (date, description, amount) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setDate(1, new java.sql.Date(date.getTime()));
            statement.setString(2, description);
            statement.setDouble(3, amount);
            statement.executeUpdate();
            System.out.println("Expense recorded successfully!");
        } catch (ParseException e) { //handle errors related to parsing operations, particularly related to date and time parsing.
            System.out.println("Invalid date format. Please use dd-MM-yyyy.");
        }
    }

    private static void viewExpenses(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM expenses";
        PreparedStatement statement = connection.prepareStatement(selectSQL);
        ResultSet resultSet = statement.executeQuery();

        System.out.println("Expense List:");
        System.out.println("ID\tDate\tDescription\tAmount");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            Date date = resultSet.getDate("date");
            String description = resultSet.getString("description");
            double amount = resultSet.getDouble("amount");

            System.out.println(id + "\t" + date + "\t" + description + "\t" + amount);
        }
    }
}
