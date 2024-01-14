package model;
import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import io.cucumber.java.en.*;
import static org.mockito.Mockito.*;
import io.cucumber.spring.CucumberContextConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
public class UserStepDefinitions {
    private User user;
    private Exception exception;

    @Given("a user has a credit of {float}")
    public void aUserHasACreditOf(float initialCredit) {
            user = new User();
            user.setCredit(initialCredit);
    }

    @Given("a user with a buy list")
    public void aUserWithABuyList() {
        user = new User();
        Map<String, Integer> buyList = new HashMap<>();
        buyList.put("1", 1);
        buyList.put("2", 4);
        user.setBuyList(buyList);
    }

    @When("attempting to add {float}")
    public void attemptingToAddCrediting(float creditAmount) {
        try {
            user.addCredit(creditAmount);
        }
        catch (InvalidCreditRange e) {
            exception = e;
        }
    }

    @When("attempting to withdraw {float}")
    public void attemptingToWithdraw(float withdrawalAmount) {
        try {
            user.withdrawCredit(withdrawalAmount);
        } catch (InsufficientCredit e) {
            exception = e;
        }
    }

    @When("the user removes an item from buy list with id {string}")
    public void theUserRemovesAnItemFromBuyListWithId(String id) {
        try {
            Commodity commodity = mock(Commodity.class);
            when(commodity.getId()).thenReturn(id);
            user.removeItemFromBuyList(commodity);
        } catch (CommodityIsNotInBuyList e) {
            exception = e;
        }
    }


    @Then("an InvalidCreditRange exception should be thrown")
    public void anInvalidCreditRangeExceptionShouldBeThrown() {
        assertThrows(InvalidCreditRange.class, () -> {
            throw exception;
        });
    }

    @Then("an InsufficientCredit exception should be thrown")
    public void anInsufficientCreditExceptionShouldBeThrown() {
        assertThrows(InsufficientCredit.class, () -> {
            throw exception;
        });
    }

    @And("the credit balance should remain {float}")
    public void theCreditBalanceShouldRemain(float expectedCredit) {
        assertEquals(expectedCredit, user.getCredit());
    }

    @Then("the new credit balance should be {float}")
    public void newCreditBalanceShouldBe(float expectedCredit) {
        float newCredit = user.getCredit();
        assertEquals(expectedCredit, newCredit);
    }

    @Then("an CommodityIsNotInBuyList exception should be thrown")
    public void anCommodityIsNotInBuyListExceptionShouldBeThrown() {
        assertThrows(CommodityIsNotInBuyList.class, () -> {
            throw exception;
        });
    }

    @Then("the item should be removed from buy list")
    public void theItemShouldBeRemovedFromBuyList() {
        assertFalse(user.getBuyList().containsKey("1"));
    }

    @Then("the quantity of the item should be decreased by 1")
    public void theQuantityOfTheItemShouldBeDecreasedBy1() {
        assertEquals(3, user.getBuyList().get("2"));
    }
}