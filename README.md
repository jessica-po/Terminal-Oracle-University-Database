# Terminal-Oracle-University-Database

Note: this is configured to run with the remote Oracle connection of a university, to use it in the same way make sure to have connection to the databse, and VPN connection on before proceeding with the next steps. 

Else, edit the JdbcOracleConnectionTemplate.java to include information about your connection

1. Edit the JdbcOracleConnectionTemplate.java to use your Oracle username and password in line 28

2. Download the ojdbc driver for your machine, if compatible use the one provided in the folder

3. Make sure JdbcOracleConnectionTemplate.java and ojdbc6.jar are in the same folder

4. run javac -cp ojdbc6.jar: JdbcOracleConnectionTemplate.java from the root folder to compile

5. Then run program with the following command java -cp ojdbc6.jar: JdbcOracleConnectionTemplate

