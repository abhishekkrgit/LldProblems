
Functional Requirement

1. Should support 3 type of transaction a. Cash Deposit, b. Cash Withdrawal,  c. balance enquiry.
2. It should handle authentication using card and pincode.
3. Should support cash of denomination 10, 20, 50, 100.
4. Track atm state transition IDLE, CARD_INSERTED, AUTHENTICATED, DISPENSING.
5. Insufficient balance in user account or insufficient amount in atm should be handle gracefully.
6. Simulate bank operation (Authentication, balance check, debit/credit);



Non-Functional Requirement
1. Design should follow object oriented principles with clear separation of concern
2. Code should be modular and extensible to support new tansaction type and denomination.
2. Code should be thread safe to handle concurrent transaction.
4. component should be testable in isolation
5. Financial operation should follow validate before commit; always verify dispensibility before debiting


Core Entities

Enum
 TransactionType, AtmState, Denomination

data class
    Card (cardno, bankAccount, pin,  )
    Transaction(type, bankAccount, timestamp)
    Account(balance, )


   