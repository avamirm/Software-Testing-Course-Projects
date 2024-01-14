Feature: Withdraw Credit

  Scenario: Attempt to withdraw more credit than available
    Given a user has a credit of 10.50
    When attempting to withdraw 100.50
    Then an InsufficientCredit exception should be thrown
    And the credit balance should remain 10.50

  Scenario: User withdraws credit successfully
    Given a user has a credit of 100.0
    When attempting to withdraw 50.0
    Then the new credit balance should be 50.0
