Feature: Interface Operation Feature

 Background: Init
  Given I want to create a VF

 Scenario: Test InterfaceOperation CRUD
    #Create Multiple operations Operations
  When I want to create an Operation
  When I want to create an Operation
  When I want to create an Operation

   #List All Operations
  When I want to list Operations

    #Get Operation By OperationId
  When I want to get an Operation by Id

   #Update Operation
  When I want to update an Operation

   #Delete Operation
  When I want to delete an Operation

  #Checkin
  When I want to checkin this component
  Then I want to check property "lifecycleState" for value "NOT_CERTIFIED_CHECKIN"

  #Certify
  Then I want to certify this component
  And I want to check property "lifecycleState" for value "CERTIFIED"

 Scenario: Test InterfaceOperation CREATE
  #Create Operations
  When I want to create an Operation
  And  I want to create an Operation
  And  I want to create an Operation

 #List All Operations
  When I want to list Operations

#Checkin
  When I want to checkin this component
  Then I want to check property "lifecycleState" for value "NOT_CERTIFIED_CHECKIN"

#Certify
  Then I want to certify this component
  And I want to check property "lifecycleState" for value "CERTIFIED"


 Scenario: Test InterfaceOperation UPDATE
    #Create Operation
  When I want to create an Operation

    #Get Operation By OperationId
  When I want to get an Operation by Id
   #Update Operation
  When I want to update an Operation

  #Checkin
  When I want to checkin this component
  Then I want to check property "lifecycleState" for value "NOT_CERTIFIED_CHECKIN"

  #Certify
  Then I want to certify this component
  And I want to check property "lifecycleState" for value "CERTIFIED"


 Scenario: Test InterfaceOperation DELETE
    #Create Operation
  When I want to create an Operation

    #Get Operation By OperationId
  When I want to get an Operation by Id

   #Delete Operation
  When I want to delete an Operation

  #Checkin
  When I want to checkin this component
  Then I want to check property "lifecycleState" for value "NOT_CERTIFIED_CHECKIN"

  #certify
  Then I want to certify this component
  And I want to check property "lifecycleState" for value "CERTIFIED"