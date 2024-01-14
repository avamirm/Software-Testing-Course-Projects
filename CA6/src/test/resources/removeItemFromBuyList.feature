Feature: Remove an item from buy list

  Scenario: Removing an item from buy list that does not exist
    Given a user with a buy list
    When the user removes an item from buy list with id "3"
    Then an CommodityIsNotInBuyList exception should be thrown

  Scenario: Removing an item from buy list that has quantity 1
    Given a user with a buy list
    When the user removes an item from buy list with id "1"
    Then the item should be removed from buy list

    Scenario: Removing an item from buy list that has quantity more than 1
    Given a user with a buy list
    When the user removes an item from buy list with id "2"
    Then the quantity of the item should be decreased by 1