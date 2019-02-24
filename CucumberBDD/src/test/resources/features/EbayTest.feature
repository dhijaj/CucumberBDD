@Ebay
Feature: Ebay Profile
  As an user of the ebay
  I want to search 'toys'
  and add to cart

  Background: User navigates to Ebay home page and Search for toys
    Given I am on the "Ebay home" page on URL "www.ebay.com.au"
    And I should see search bar
    When I fill in "SearchBar" with "toys"
    And I click on the "Search" button
    Then I am on the "SearchResults" page
    And I should see the list of Available toys


  Scenario: Adding first item of the result to cart
    When I click on the "first" item on "searchResultItem"
    And I click "AddToCart"
    Then I am on cart page with the added item
    And the items count on cart is 1

  Scenario: Adding Second item of the result to cart
    When I click on the "Second" item on "searchResultItem"
    And I click "AddToCart"
    Then I am on cart page with the added item
    And the items count on cart is 2