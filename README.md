# Money-Transfer-Application
A simple restful money transfer application using vert.x framework

# Money-Transfer-Application
A simple restful money transfer application using vert.x framework

Currently the APIs supported are :

1. To create user and account :
   POST /user/create-account
   Request Body -
   {
   "userName":"user1",
   "userId":"user1",
   "userAccountType":"Savings",
   "balance":200
   }

2. Deposit amount to account :
   POST user/:userId/deposit
   Request Body -
   {
	  "toAccount":"1",
	  "amount":200
   }
   
3. Withdraw from account :
   POST user/:userId/withdraw
   Request Body -
   {
	  "fromAccount":"1",
	  "amount":200
   }

4. Transfer money between different account of same user :
   POST user/:userId/transfer-money
   Request Body -
   {
   "toAccount":"1",
	 "fromAccount":"2",
	 "amount":100
   }

5. Transfer money between different account of different user :
   POST user/:userId/transfer-money
   Request Body -
   {
	  "toAccount":"1",
	  "toAccountUser":"userX",
	  "fromAccount":"2",
	  "amount":100
   }
  
6. Get user account details :
   POST user/:userId/account/:accountId
   
