import java.sql.Connection;
import java.util.Scanner;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;
 
/**
 * This program demonstrates how to make database connection with Oracle
 
 *
 */
public class JdbcOracleConnectionTemplate {
 
    public static void main(String[] args) {
 
        Connection conn1 = null;
        Scanner scanner = new Scanner(System.in);

        try {
            // registers Oracle JDBC driver - though this is no longer required
            // since JDBC 4.0, but added here for backward compatibility
            Class.forName("oracle.jdbc.OracleDriver");
 
           
            String dbURL1 = "jdbc:oracle:thin:username/password@oracle.scs.ryerson.ca:1521:orcl";  // that is school Oracle database and you can only use it in the labs
																						

            // String dbURL1 = "jdbc:oracle:thin:username/password@localhost:1521:xe";
			/* This XE or local database that you installed on your laptop. 1521 is the default port for database, change according to what you used during installation. 
			xe is the sid, change according to what you setup during installation. */
			
			conn1 = DriverManager.getConnection(dbURL1);
            if (conn1 != null) {
                System.out.println("Connected with connection #1");
            }
 
            boolean open = true;
            while (open) {
                System.out.println("\nChoose an operation:");
                System.out.println("1. Create tables");
                System.out.println("2. Populate tables");
                System.out.println("3. Drop tables");
                System.out.println("4. Query");
                System.out.println("5. Exit");
                System.out.print("Please select an option from above: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        createTables(conn1);
                        break;
                    case 2:
                        insertData(conn1);
                        break;
                    case 3:
                        dropTables(conn1);
                        break;
                    case 4:
                        runQuery(conn1);
                        // System.out.println("query");
                        break;
                    case 5:
                        open = false;
                        System.out.println("Exiting program...");
                        break;
                    default:
                        System.out.println("Invalid choice. Choose an option from the numbers provided.");
                        break;
                }
            }
			
		
            //In your database, you should have a table created already with at least 1 row of data. In this select query example, table testjdbc was already created with at least 2 rows of data with columns NAME and NUM.
			//When you enter your data into the table, please make sure to commit your insertions to ensure your table has the correct data. So the commands that you need to type in Sqldeveloper are
			// CREATE TABLE TESTJDBC (NAME varchar(8), NUM NUMBER);
            // INSERT INTO TESTJDBC VALUES ('ALIS', 67);
            // INSERT INTO TESTJDBC VALUES ('BOB', 345);
            // COMMIT;
			
			String query = "select NAME, NUM from TESTJDBC";
							
			try (Statement stmt = conn1.createStatement()) {

			ResultSet rs = stmt.executeQuery(query);

			//If everything was entered correctly, this loop should print each row of data in your TESTJDBC table.
			// And you should see the results as follows:
			// Connected with connection #1
			// ALIS, 67
			// BOB, 345
			
			while (rs.next()) {
				String name = rs.getString("NAME");
				int num = rs.getInt("NUM");
				System.out.println(name + ", " + num);
			}
			} catch (SQLException e) {
				System.out.println(e.getErrorCode());
			}


 
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn1 != null && !conn1.isClosed()) {
                    conn1.close();
                }
     
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
			

    }


    private static void createTables(Connection conn) {
        String[] createTableSQL = {
            "CREATE TABLE University (" +
            "University_Name VARCHAR(255) NOT NULL, " +
            "Address VARCHAR(255), " +
            "Phone_Number VARCHAR(20) UNIQUE, " +
            "University_Email VARCHAR(255) UNIQUE, " +
            "PRIMARY KEY (University_Name))",

            "CREATE TABLE Student (" +
            "Student_Number INT NOT NULL, " +
            "Student_Name VARCHAR(255) NOT NULL, " +
            "SIN INT UNIQUE, " +
            "DOB DATE, " +
            "Student_Email VARCHAR(255) UNIQUE, " +
            "University VARCHAR(255) NOT NULL, " +
            "PRIMARY KEY (Student_Number), " +
            "FOREIGN KEY (University) REFERENCES University(University_Name))",

            "CREATE TABLE Professor (" +
            "Professor_ID INTEGER NOT NULL, " +
            "Professor_Name VARCHAR(255) NOT NULL, " +
            "Professor_Email VARCHAR(255) UNIQUE, " +
            "University VARCHAR(255) NOT NULL, " +
            "PRIMARY KEY (Professor_ID), " +
            "FOREIGN KEY (University) REFERENCES University(University_Name))",

            "CREATE TABLE Teaching_Assistant (" +
            "TA_ID INTEGER NOT NULL, " +
            "TA_Name VARCHAR(255) NOT NULL, " +
            "TA_Email VARCHAR(255) UNIQUE, " +
            "Professor INTEGER, " +
            "PRIMARY KEY (TA_ID), " +
            "FOREIGN KEY (Professor) REFERENCES Professor(Professor_ID))",

            "CREATE TABLE Course (" +
            "Course_Code VARCHAR(255) NOT NULL, " +
            "Course_Name VARCHAR(255), " +
            "Coordinator INT UNIQUE, " +
            "Fee DECIMAL(10, 2), " +
            "Num_Students INT, " +
            "Credit DECIMAL(5, 2) DEFAULT 1.00, " +
            "Term VARCHAR(255), " +
            "PRIMARY KEY (Course_Code), " +
            "FOREIGN KEY (Coordinator) REFERENCES Professor(Professor_ID))",

            "CREATE TABLE Section (" +
            "Section_Number INT NOT NULL, " +
            "Open_Slots INT, " +
            "TA INT, " +
            "PRIMARY KEY (Section_Number), " +
            "FOREIGN KEY (TA) REFERENCES Teaching_Assistant(TA_ID))",

            "CREATE TABLE Prerequisite (" +
            "Prerequsite_ID VARCHAR(255) NOT NULL, " +
            "Course_ID  VARCHAR(255) NOT NULL, " +
            "PRIMARY KEY(Prerequsite_ID), " +
            "FOREIGN KEY (Course_ID) REFERENCES Course(Course_Code))",

            "CREATE TABLE Academic_Status (" +
            "Student_Number INT NOT NULL," +
            "Study_Year INT DEFAULT 1," +
            "GPA DECIMAL (10,2) ," +
            "Credits_Earned DECIMAL (10,2), PRIMARY KEY (Student_Number)," +
            "FOREIGN KEY (Student_Number) REFERENCES Student (Student_Number))",

            "CREATE TABLE Student_Fees (" +
            "Student_Number INT NOT NULL," +
            "Fees DECIMAL (10,2)," +
            "PRIMARY KEY (Student_Number)," +
            "FOREIGN KEY (Student_Number) REFERENCES Student (Student_Number))",

            "CREATE TABLE Course_Taken (" +
            "Record_ID INT NOT NULL, " +
            "GPA Decimal(10,2), " +
            "Enroll_Date DATE, " +
            "Drop_Date DATE, " +
            "Course_Code VARCHAR(255), " +
            "Student_Number INT, " +
            "PRIMARY KEY (Record_ID), " +
            "FOREIGN KEY (Course_Code) REFERENCES Course(Course_Code), " +
            "FOREIGN KEY (Student_Number) REFERENCES Student(Student_Number))",

            "CREATE TABLE ADVISOR (" +
            "Program VARCHAR(255) NOT NULL," +
            "Advisor_ID INT NOT NULL," +
            "Advisor_Name VARCHAR(255) NOT NULL," +
            "PRIMARY KEY (Program, Advisor_ID))"

        };

        try (Statement temp = conn.createStatement()) {
            for (String createSQL : createTableSQL) {
                temp.executeUpdate(createSQL);
            }
            System.out.println("University table created successfully.");
            System.out.println("Student table created successfully.");
            System.out.println("Professor table created successfully.");
            System.out.println("Teaching Assistant table created successfully.");
            System.out.println("Course table created successfully.");
            System.out.println("Section table created successfully.");
            System.out.println("Prerequisite table created successfully.");
            System.out.println("Academic status table created successfully.");
            System.out.println("Student fees table created successfully.");
            System.out.println("Course taken table created successfully.");
            System.out.println("Advisor table created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }


    private static void dropTables(Connection conn) {
        String[] dropTableSQL = {
            "DROP TABLE Section CASCADE CONSTRAINTS",
            "DROP TABLE Prerequisite CASCADE CONSTRAINTS",
            "DROP TABLE Academic_Status CASCADE CONSTRAINTS",
            "DROP TABLE Course_Taken CASCADE CONSTRAINTS",
            "DROP TABLE Teaching_Assistant CASCADE CONSTRAINTS",
            "DROP TABLE Course CASCADE CONSTRAINTS",
            "DROP TABLE Professor CASCADE CONSTRAINTS",
            "DROP TABLE Advisor CASCADE CONSTRAINTS",
            "DROP TABLE Student_Fees CASCADE CONSTRAINTS",
            "DROP TABLE Student CASCADE CONSTRAINTS",
            "DROP TABLE University CASCADE CONSTRAINTS"
        };
    
        try (Statement temp = conn.createStatement()) {
            for (String dropSQL : dropTableSQL) {
                try {
                    temp.executeUpdate(dropSQL);
                    System.out.println("Dropped table successfully.");
                } catch (SQLException e) {
                    System.out.println("Error dropping table" + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error dropping tables: " + e.getMessage());
        }
    }


    private static void insertData(Connection conn) {
        // SQL for inserting data
        String[] insertDataSQL = {
            "INSERT INTO University (University_Name, Address, Phone_Number, University_Email) VALUES ('Toronto Metropolitan University', '350 Victoria St, Toronto, ON, M2B2K3', '(416) 979-5000', 'info@torontomu.ca')",
            "INSERT INTO University (University_Name, Address, Phone_Number, University_Email) VALUES ('University of Toronto', '27 Kings College Cir, Toronto, ON M5S 1A1', '(416) 978-2011', 'uoft@torontou.ca')",
            "INSERT INTO Professor (Professor_ID, Professor_Name, Professor_Email, University) VALUES(224952123, 'Abdolreza Abhari', 'aabhari@torontomu.ca', 'University of Toronto')",
            "INSERT INTO Professor (Professor_ID, Professor_Name, Professor_Email, University) VALUES(226789101, 'Marcus Santos', 'marcus.santos@torontomu.ca', 'Toronto Metropolitan University')",
            "INSERT INTO Professor(Professor_ID, Professor_Name, Professor_Email, University) VALUES(221230981, 'Elodie Lugez', 'elodie.lugez@torontomu.ca', 'Toronto Metropolitan University')",
            "INSERT INTO Student (Student_Number, Student_Name, SIN, DOB, Student_Email, University) VALUES(50117368291, 'James_Wood', 123456789, TO_DATE('10-05-2004', 'DD-MM-YYYY'), 'james.wood@torontomu.ca', 'Toronto Metropolitan University')",
            "INSERT INTO Student (Student_Number, Student_Name, SIN, DOB, Student_Email, University) VALUES(50116742865, 'John_Doe', 987654321, TO_DATE('25-01-2002', 'DD-MM-YYYY'), 'john.doe@torontomu.ca','University of Toronto')",
            "INSERT INTO Student(Student_Number, Student_Name, SIN, DOB, Student_Email, University) VALUES(50112053864, 'Tom_Ford', 285174245, TO_DATE('30-09-1998', 'DD-MM-YYYY'), 'Tom.ford@torontomu.ca', 'Toronto Metropolitan University')",
            "INSERT INTO Teaching_Assistant (TA_ID, TA_Name, TA_Email, Professor) VALUES(881042694, 'Sam Johnson', 'sam.johnson@torontomu.ca', 224952123)",
            "INSERT INTO Teaching_Assistant(TA_ID, TA_Name, TA_Email, Professor) VALUES(881846275, 'Kathryn Lovegood', 'kathryn.lovegood@torontomu.ca', 226789101)",
            "INSERT INTO Teaching_Assistant(TA_ID, TA_Name, TA_Email, Professor) VALUES(882941842, 'Josh Banks', 'josh.banks@torontomu.ca', 221230981)",
            "INSERT INTO Course (Course_Code, Course_Name, Coordinator, Fee, Num_Students, Credit, Term) VALUES ('CPS 510', 'Database Systems 1', 224952123, 780.00, 250, DEFAULT, 'Fall')",
            "INSERT INTO Course (Course_Code, Course_Name, Coordinator, Fee, Num_Students, Credit, Term) VALUES ('CPS 305', 'Data Structures and Algorithms', 226789101, 700.00, 420, DEFAULT, 'Fall')",
            "INSERT INTO Course (Course_Code, Course_Name, Coordinator, Fee, Num_Students, Credit, Term) VALUES ('CPS 803', 'Machine Learning', 221230981, 1000.00, 60, 0.50, 'Fall')",
            "INSERT INTO Section(Section_Number, Open_Slots, TA) VALUES (1, 200, 881042694)",
            "INSERT INTO Section(Section_Number, Open_Slots, TA) VALUES (2, 200, 881846275)",
            "INSERT INTO Section(Section_Number, Open_Slots, TA) VALUES (7, 300, 882941842)",
            "INSERT INTO Prerequisite(Prerequsite_ID, Course_ID) VALUES('CPS 305', 'CPS 510')",
            "INSERT INTO Prerequisite(Prerequsite_ID, Course_ID) VALUES('CPS 393', 'CPS 803')",
            "INSERT INTO Prerequisite(Prerequsite_ID, Course_ID) VALUES('CPS 209', 'CPS 305')",
            "INSERT INTO Academic_Status(Student_Number, Study_Year, GPA, Credits_Earned) VALUES(50117368291, 3, 4.33, 30)",
            "INSERT INTO Academic_Status(Student_Number, Study_Year, GPA, Credits_Earned) VALUES(50116742865, 1, 1.67, 10)",
            "INSERT INTO Academic_Status(Student_Number, Study_Year, GPA, Credits_Earned) VALUES(50112053864, 2, 3.67, 20)",
            "INSERT INTO Student_Fees(Student_Number, Fees) VALUES (50117368291, 5726)",
            "INSERT INTO Student_Fees(Student_Number, Fees) VALUES (50116742865, 1000)",
            "INSERT INTO Student_Fees(Student_Number, Fees) VALUES (50112053864, 2852)",
            "INSERT INTO Course_Taken(Record_ID, GPA, Enroll_Date, Drop_Date, Course_Code, Student_Number) VALUES(341257890, 3.6, TO_DATE('12-08-2022', 'DD-MM-YYYY'), NULL, 'CPS 305', 50117368291)",
            "INSERT INTO Course_Taken(Record_ID, GPA, Enroll_Date, Drop_Date, Course_Code, Student_Number) VALUES(341259800, 4.0, TO_DATE('14-08-2023', 'DD-MM-YYYY'), NULL, 'CPS 510', 50117368291)",
            "INSERT INTO Course_Taken(Record_ID, GPA, Enroll_Date, Drop_Date, Course_Code, Student_Number) VALUES(341253570, 3.0, TO_DATE('09-08-2024', 'DD-MM-YYYY'), NULL, 'CPS 803', 50117368291)",
            "INSERT INTO Course_Taken(Record_ID, GPA, Enroll_Date, Drop_Date, Course_Code, Student_Number) VALUES(341251235, 3.1, TO_DATE('14-08-2023', 'DD-MM-YYYY'), NULL, 'CPS 305', 50116742865)",
            "INSERT INTO Course_Taken(Record_ID, GPA, Enroll_Date, Drop_Date, Course_Code, Student_Number) VALUES(341251777, NULL, TO_DATE('09-08-2024', 'DD-MM-YYYY'), TO_DATE('15-09-2024', 'DD-MM-YYYY'), 'CPS 803', 50116742865)", 
            "INSERT INTO Course_Taken(Record_ID, GPA, Enroll_Date, Drop_Date, Course_Code, Student_Number) VALUES(341250007, 3.8, TO_DATE('09-08-2024', 'DD-MM-YYYY'), NULL, 'CPS 305', 50112053864)",
            "INSERT INTO Advisor(Program, Advisor_ID, Advisor_Name) VALUES('Computer Science', 123789564, 'Anderson Green')",
            "INSERT INTO Advisor(Program, Advisor_ID, Advisor_Name) VALUES('Engineering', 395729485, 'Bob Duncan')",
            "INSERT INTO Advisor(Program, Advisor_ID, Advisor_Name) VALUES('Business', 1947738573, 'Matthew Smith')"

        };

        try (Statement temp = conn.createStatement()) {
            for (String insertSQL : insertDataSQL) {
                temp.executeUpdate(insertSQL);
                System.out.println("Data inserted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    private static void runQuery(Connection conn){
        Scanner s = new Scanner(System.in);
        boolean running = true;
        while (running){
            System.out.println("\n\nSelect a Query to Run:\n");
            System.out.println("1. Query 1");
            System.out.println("2. Query 2");
            System.out.println("3. Query 3");
            System.out.println("4. Query 4");
            System.out.println("5. Query 5");
            System.out.println("6. Custom Query");
            System.out.println("7. Exit\n\n");

            System.out.print("Please select an option from above: ");
            int choice = s.nextInt();
            s.nextLine(); 

            System.out.println("\n");

            switch (choice) {
                case 1:
                    Query1(conn);
                    break;
                case 2:
                    Query2(conn);
                    break;
                case 3:
                    Query3(conn);
                    break;
                case 4:
                    Query4(conn);
                    break;
                case 5:
                    Query5(conn);
                    break;
                case 6:
                    customQuery(conn, s);
                    break;
                case 7:
                    System.out.println("Exiting Query Menu");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid input. Please select from the options above.");
                    break;
            }
        }
    }


    private static void Query1(Connection conn){
        String Q1 =
            "SELECT University, COUNT(Student_Number) AS Total_Students " +
            "FROM Student " +
            "GROUP BY University " +
            "UNION " +
            "SELECT University_Name, 0 AS Total_Students " +
            "FROM University " +
            "WHERE University_Name NOT IN (SELECT University FROM Student)";

        try (Statement temp = conn.createStatement()) {
            ResultSet rs = temp.executeQuery(Q1);

                System.out.println("University | Total Students\n" +
                                    "-------------------------\n");
                while (rs.next()) {
                    String university = rs.getString("University");
                    int totalStudents = rs.getInt("Total_Students");
                    System.out.println(university + " | " + totalStudents + "\n");
                }
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }


    private static void Query2(Connection conn){
        String Q2 =
            "SELECT Student_Name, TO_CHAR(Student_Number, '99999999999999999999') AS Student_Number " +
            "FROM Student s " +
            "WHERE NOT EXISTS " +
            "(SELECT ar.Student_Number " +
            "FROM Academic_Status ar " +
            "WHERE s.Student_Number = ar.Student_Number " +
            "AND ar.GPA > 3.6)";

        try (Statement temp = conn.createStatement()) {
            ResultSet rs = temp.executeQuery(Q2);

                System.out.println("Student Name " + " | " + "Student Number\n" +
                                    "-----------------------------------------\n");
                while (rs.next()) {
                    String std_name = rs.getString("Student_Name");
                    String std_number = rs.getString("Student_Number");
                    System.out.println(std_name + "     | " + std_number + "\n");
                }
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }


    private static void Query3(Connection conn){
        String Q3 = 
        "SELECT a.Program, a.Advisor_Name " +
        "FROM Advisor a " +
        "WHERE a.Advisor_Name IS NOT NULL " +
        "ORDER BY a.Program";

    try (Statement temp = conn.createStatement()) {
        ResultSet rs = temp.executeQuery(Q3);

        System.out.println("Program | " + "Advisor Name\n" +
                           "---------------------------\n");
        while (rs.next()) {
            String program = rs.getString("Program");
            String advisorName = rs.getString("Advisor_Name");
            System.out.println(program + " | " + advisorName + "\n");
        }
    } catch (SQLException e) {
        System.out.println("Error executing Query 6: " + e.getMessage());
    }
    }

    private static void Query4(Connection conn){
        String Q4 =
            "SELECT ct.Course_Code, COUNT(DISTINCT s.Student_Number) AS Distinct_Student " +
            "FROM Course_Taken ct, Student s " +
            "WHERE ct.Student_Number = s.Student_Number " +
            "GROUP BY ct.Course_Code";

        try (Statement temp = conn.createStatement()) {
            ResultSet rs = temp.executeQuery(Q4);

                System.out.println("Course Code" + " | " + "Distinct Student Numbers\n" +
                                    "------------------------------------------------\n");
                while (rs.next()) {
                    String c_code = rs.getString("Course_Code");
                    int std_number = rs.getInt("Distinct_Student");
                    System.out.println(c_code + " | " + std_number + "\n");
                }
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }


    private static void Query5(Connection conn){
        String Q5 =
            "SELECT s.Student_Name, ROUND(AVG(ct.GPA),2) AS AVG_GPA " +
            "FROM Student s, Course_Taken ct " +
            "WHERE s.Student_Number = ct.Student_Number " +
            "GROUP BY s.Student_Name " +
            "HAVING AVG(ct.GPA) > 2";

        try (Statement temp = conn.createStatement()) {
            ResultSet rs = temp.executeQuery(Q5);

                System.out.println("Student Name | " + "Avg GPA\n" +
                                    "---------------------------\n");
                while (rs.next()) {
                    String std_name = rs.getString("Student_Name");
                    double gpa = rs.getDouble("Avg_GPA");
                    System.out.println(std_name + " | " + gpa + "\n");
                }
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }    

    private static void customQuery(Connection conn, Scanner s){
        System.out.println("Enter your custom SQL query:");
        String userQuery = s.nextLine();
    
        try (Statement temp = conn.createStatement()) {
            ResultSet rs = temp.executeQuery(userQuery);
            
            while (rs.next()) {
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }




}