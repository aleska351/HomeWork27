import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class JDBC {

    public static Connection getConnection() throws SQLException, IOException {

        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
            properties.load(in);
        }
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        System.out.println("Подключение успешно");
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Creates a table for student.
     *
     * @param connection The JDBC connection, opened and prepared.
     */
    private static void createStudentsTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS student " +
                "(id INTEGER PRIMARY KEY AUTO_INCREMENT , name VARCHAR(100)" +
                ", avgMark FLOAT, groupName VARCHAR(10), course TINYINT )");
    }

    /**
     * Removes the table for students with all its content.
     *
     * @param connection The JDBC connection, opened and prepared.
     */
    private static void deleteStudentsTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS student");
    }

    /**
     * Inserts new student into the database.
     *
     * @param connection The JDBC connection, opened and prepared.
     * @param student    The student to insert.
     */
    private static void insertStudent(Connection connection, Student student) throws SQLException {

        PreparedStatement statements =
                connection.prepareStatement("INSERT INTO student(name, avgMark,groupName, course ) VALUES(?, ?, ?, ?)");
        statements.setString(1, student.name);
        statements.setFloat(2, student.avgMark);
        statements.setString(3, student.groupName);
        statements.setInt(4, student.course);
        statements.executeUpdate();
    }

    /**
     * Delete student with such ID
     *
     * @param connection The JDBC connection, opened and prepared.
     * @param id         student id
     */
    private static void deleteStudentWithId(Connection connection, int id) throws SQLException {
        PreparedStatement statements =
                connection.prepareStatement("DELETE FROM student WHERE id = " + id);
        statements.executeUpdate();
    }

    /**
     * Print all students
     *
     * @param cursor
     */
    private static void printStudents(ResultSet cursor) throws SQLException {
        System.out.printf("%-5s | %-10s | %-10s | %-10s | %-5s %n",
                "ID", "NAME", "AVG MARK", "GROUP NAME", "COURSE");

        while (cursor.next()) {
            System.out.printf("%-5d | %-10s | %-10.2f | %-10s | %-5d",
                    cursor.getInt("id"),
                    cursor.getString("name"),
                    cursor.getFloat("avgMark"),
                    cursor.getString("groupName"),
                    cursor.getInt("course"));
            System.out.println();
        }
    }

    /**
     * Fetches all students from the database.
     *
     * @param connection The JDBC connection, opened and prepared.
     */
    private static void printAllStudents(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try (ResultSet cursor = statement.executeQuery(
                "SELECT * FROM student " +
                        "ORDER BY name ASC, id DESC")) {
            printStudents(cursor);
        }
    }

    /**
     * Prints all students, whose name contains the given text.
     *
     * @param connection The JDBC connection, opened and prepared.
     * @param text       The text to match students by.
     */
    public static void printStudentsWithName(Connection connection, String text) throws SQLException {
        Statement statement = connection.createStatement();
        try (ResultSet cursor = statement.executeQuery(
                "SELECT * FROM student WHERE name LIKE '%" + text + "%'")) {
            printStudents(cursor);
        }
    }

    /**
     * Prints all students, whose course as given by user.
     *
     * @param connection The JDBC connection, opened and prepared.
     * @param course     - student course, entered by user
     */
    public static void printStudentsWithCourse(Connection connection, int course) throws SQLException {
        Statement statement = connection.createStatement();
        try (ResultSet cursor = statement.executeQuery(
                "SELECT * FROM student WHERE course = " + course)) {
            printStudents(cursor);
        }
    }

    /**
     * Prints students with given ID
     *
     * @param connection The JDBC connection, opened and prepared.
     * @param id         student ID
     * @return response that was found student with such ID
     */
    public static void printsStudentsWithID(Connection connection, int id) throws SQLException {
        Statement statement = connection.createStatement();
        try (ResultSet cursor = statement.executeQuery(
                "SELECT * FROM student WHERE id = " + id)) {
            printStudents(cursor);
        }
    }

    /**
     * This method return the response that student with such ID is present in the database.
     * @param connection The JDBC connection, opened and prepared.
     * @param id Its id that output user
     * @return response that student with such ID is present in the database
     */
    public static boolean idIsAvailable(Connection connection, int id) throws SQLException {
        Statement statement = connection.createStatement();
        int x = 0;
        try (ResultSet cursor = statement.executeQuery(
                "SELECT * FROM student WHERE id = " + id)) {
            while (cursor.next()) {
                x = cursor.getInt("id");
            }
        }
        return (x > 0);
    }
    /**
     * This method return the response that students with such part of name is present in the database.
     * @param connection The JDBC connection, opened and prepared.
     * @param text Its text that output user.
     * @return response that student with such part of name is present in the database.
     */
    public static boolean nameIsAvailable(Connection connection, String text) throws SQLException {
        Statement statement = connection.createStatement();
        String s = null;
        try (ResultSet cursor = statement.executeQuery(
                "SELECT * FROM student WHERE name LIKE '%" + text + "%'")) {
            while (cursor.next()) {
                s = cursor.getString("name");
            }
        }
        return (s != null);
    }
    /**
     * This method return the response that students with such course is present in the database.
     * @param connection The JDBC connection, opened and prepared.
     * @param course Its course that output user
     * @return response that student with such course is present in the database
     */

    public static boolean courseIsAvailable(Connection connection, int course) throws SQLException {
        Statement statement = connection.createStatement();
        int x = 0;
        try (ResultSet cursor = statement.executeQuery(
                "SELECT * FROM student WHERE course =" + course)) {
            while (cursor.next()) {
                x = cursor.getInt("course");
            }
        }
        return (x > 0);
    }

    //огромнейший метод с кучей проверок

    /**
     * This method implements console menu output with user selection and processing of his answers
     * @param scanner
     * @param connection The JDBC connection, opened and prepared.
     */
    public static void consoleMenu(Scanner scanner, Connection connection) throws SQLException {
        String s;
        while (true) {
            System.out.println("1. Для создания таблицы введите 1");
            System.out.println("2. Для удаления таблицы введите 2");
            System.out.println("3. Для добавления нового студента введите 3");
            System.out.println("4. Для вывода всех студентов, отсортированных по имени от А до Я введите 4");
            System.out.println("5. Чтобы выполнить поиск студента по имени введите 5");
            System.out.println("6. Чтобы выполнить поиск студента по курсу введите 6");
            System.out.println("7. Чтобы выполнить поиск студента по ID введите 7");
            System.out.println("8. Для удаления студента по ID  введите 8");
            System.out.println("Для выхода нажмите 'Q' ");
            s = scanner.next();

            int id;
            String name;
            float avg;
            String groupName;
            int course;
            switch (s) {
                case "1":
                    createStudentsTable(connection);
                    System.out.println("Таблица создана");
                    break;
                case "2":
                    deleteStudentsTable(connection);
                    System.out.println("Таблица удалена");
                    break;
                case "3":
                    System.out.println("Для добавления студента введите его имя (от 2х до 100 символов) и нажмите Enter");
                    s = scanner.next();
                    if (s.length() >= 2 && s.length() <= 100) { //проверяем условие по длине
                        name = s;
                    } else {
                        System.out.println("Вы ввели некорректное имя, попробуйте снова");
                        break;
                    }
                    System.out.println("Добавьте средний бал (от 1.0 до 100.0) и нажмите Enter");
                    float mark;
                    if (scanner.hasNextFloat()) { // проверяем является бал числом
                        mark = scanner.nextFloat();
                        if (mark >= 1.0 && mark <= 100.0) { // проверяем соответсвует ли он условия
                            avg = mark;
                        } else {
                            System.out.println("Вы ввели некорректный средний бал, попробуйте снова (от 1.0 до 100.0)");
                            break;
                        }
                    } else {
                        System.out.println("Вы ввели не число, попробуйсте снова");
                        break;
                    }
                    System.out.println("Добавьте имя группы (от 2 до 10 символов ) и нажмите Enter");
                    s = scanner.next();
                    if (s.length() >= 2 && s.length() <= 10) {
                        groupName = s;
                    } else {
                        System.out.println("Вы ввели некорректное имя группы, попробуйте снова");
                        break;
                    }
                    System.out.println("Добавьте курс (от 1 до 6) и нажмите Enter");
                    int c;
                    if (scanner.hasNextInt()) {
                        c = scanner.nextInt();
                        if (c >= 1 && c <= 6) {
                            course = c;
                        } else {
                            System.out.println("Вы ввели некорректный курс, попробуйте снова(от 1 до 6)");
                            break;
                        }
                    } else {
                        System.out.println("Вы ввели не число, попробуйсте снова");
                        break;
                    }
                    insertStudent(connection, new Student(name, avg, groupName, course));
                    break;
                case "4":
                    printAllStudents(connection);
                    break;
                case "5":
                    System.out.println("Для поиска студента введите текст и нажмите Enter");
                    s = scanner.next();
                    if (s.length() >= 1 && s.length() <= 100) {
                        if (nameIsAvailable(connection, s)) {
                            printStudentsWithName(connection, s);
                        } else System.out.println("В базе нет студента с таким именем");
                    } else {
                        System.out.println("Ваш запрос некорректный, пожалуйста введите символы");
                    }
                    break;
                case "6":
                    System.out.println("Для поиска студента введите курс (от 1 до 6) и нажмите Enter");
                    if (scanner.hasNextInt()) {
                        course = scanner.nextInt();
                        if (course >= 1 && course <= 6) {
                            if (courseIsAvailable(connection, course)) {
                                printStudentsWithCourse(connection, course);
                            } else System.out.println("На выбранном курсе нет студентов в базе данных");
                            break;
                        } else {
                            System.out.println("Вы ввели не корректный курс");
                        }
                    } else {
                        System.out.println("Вы ввели не число");
                    }
                    break;
                case "7":
                    System.out.println("Для поиска студента введите ID и нажмите Enter");
                    if (scanner.hasNextInt()) {
                        id = scanner.nextInt();
                        if (idIsAvailable(connection, id)) {
                            printsStudentsWithID(connection, id);
                        } else {
                            System.out.println("Студента с таким ID нет в базе данных");
                            break;
                        }
                    } else {
                        System.out.println("Вы ввели не число");
                        break;
                    }
                    break;
                case "8":
                    System.out.println("Для удаления студента  введите его  ID и нажмите Enter");
                    if (scanner.hasNextInt()) {
                        id = scanner.nextInt();
                        if (idIsAvailable(connection, id)) {
                            deleteStudentWithId(connection, id);
                            System.out.println("Студент с ID " + id + " успешно удален");
                        } else {
                            System.out.println("Студента с таким ID не существует");
                            break;
                        }
                    } else {
                        System.out.println("Вы ввели не число");
                        break;
                    }
                case "Q":
                case "q": {
                    System.out.println("До свидания!");
                    return;
                }
                default: {
                    System.out.println("Вы выбрали не корректный пункт меню, повторите пожалуйста свой выбор!");
                }
            }
        }
    }
}