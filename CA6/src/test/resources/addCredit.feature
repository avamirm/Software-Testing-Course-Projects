Feature: Add Credit

  Scenario: Attempt to add less than 0 credit
    Given a user has a credit of 10.50
    When attempting to add -20.10
    Then an InvalidCreditRange exception should be thrown
    And the credit balance should remain 10.50

  Scenario: Attempt to add more than zero or zero credit
    Given a user has a credit of 10.50
    When attempting to add 20.10
    Then the new credit balance should be 30.60