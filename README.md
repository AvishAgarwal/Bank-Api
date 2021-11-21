# Eltropy_Assignment
By Avish Agarwal
****
To stepup the project we just have to clone the project and open in IDE(I used IntelliJ), make sure you have JDK installed. The data base used is of Postgres and to create it just copy paste the text inside the bank_db.sql file in the postgres terminal. To start the server run the BankApiApplicaion.java class.
****
I will be including the approach which I used to solve the given problem statement. 

### Sign in/out as admin + sign-in/out as an employee
I have tacked both problems in a single API. Both admin and employee can log in via the same endpoint. Regarding the access to particular resources, Admin has access to all the resources but the employee has access to resources related to the user. In the database, I mentioned employee and admin as different roles in the bt_employees table which we can call according to our choice. 

### Add bank employees
This resource is only accessible to the admin, we can attach an employee to a phone number that needs to be unique. 

### Delete Employee
What I am doing here is soft deleting a particular employee, which is marking it as deleted in the bt_employee table.  

### Create Customer
This same as the creation of an employee, but things to point out here is that the user is marked as unverified as KYC status initially, and no bank account is linked to it.

### Create Account + Link Account to user
I have handled both these problems together because what approach I used is account can’t exist with a user so we need to create an account and link it to a user in the same call. To create an account, we need to specify the type of account needed and the initial amount. Only four types of accounts are available saving, current, loan, and salary. If any account is not connected to the user then the account number for that particular type is kept as 0. We also need to keep in mind account can be attached to a user if the KYC is not verified.

### Update KYC Status
I have divided status into three categories verified, unverified, and rejected. We can update them from any status to any other status.

### Get details of a customer
I am giving user info and bank account linked to users and their balance as a response.

### Delete customer
Soft deleting same as in the case of employees, but here we are also soft deleting the account linked to the user.
 
### Get account balance.
Giving balance for an account as a response

### Transfer Money from one account to another.
I checked the validity of both the account and check the balance of the account from which the amount needs to be deducted. Then it creates a transaction in db and updates the related balances.  
 
### Print Account statement in Pdf
I just got the matching transaction from the bt_transactions table between a particular range and add them to the pdf which is been created using Itext and saved locally.
 
### Calculate interest for the money annually (at 3.5% p.a.) and update the account balance
There are assumptions that I made here is that the API is run by either employee or admin and the interest is deducted from the account which is attached to the bank itself. I am checking if the last interest was added one year back.  
 
 
 
By Avish Agarwal
