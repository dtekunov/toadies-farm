## Toads farm project

This project is made with a fun purpose to create a service, that is not 
typically being created with a functional language like Scala.

The project itself is a simulation of 'behavior' of 'toads' in a closed system (called 'farm' here) with given rules.


### Requirements (to run project locally): 

1. MongoDB database launched locally (configuration can be changed via configuration file)
2. Scala 2.13.4
3. Any service/utility that can allow you to comfortably send HTTP requests

### Rules of a simulation:

#### 1. Upper-level interface

At the start of the project, a 'Farm' must be created. User may have any number of farms.
Simulation on each farm is independent and unique.

Each 'Farm' has the following format:

	{
		"id": "7bdfd89a-d1ab-457e-b0aa-1221932c7920",
		"name": "farm1",
		"mode": "creative",
		"isCannibal": false,
		"mutationsModifier": 1
	}

Where "id" is a UUID, unique to each farm,"name" is a non-unique name of a farm,
"mode" is a game mode that can be *creative* (with unlimited funds and operations)
and *survival* (with limited funds and operation [IN PROGRESS]), "isCannibal" is a rule that determines
whether you will have cannibal toads that can eat other toads or no. 
"mutationsModifier" determines the probability of possible mutations, that may happen with your toads.

You may check all your farms with:

    GET: .../interface/available-farms

You may create a new farm with: (After creation, the farm becomes 'current' automatically)

    POST: /interface/new-farm?name=farm1&mode=creative&is_cannibal=false

You may change your 'current' farm with:

    GET: http://127.0.0.1:8080/interface/use-farm?name=farm1

You may stop using 'current' farm with:

    POST: .../interface/stop-using-farm

You may delete any farm you previously created with:

    DELETE: .../interface/delete-farm?name=farm1

#### 2. Actions-level interface in CREATIVE mode

Every farm has an 'owner' of a farm  with the following format:

    {
	    "id": "0720ad6d-7110-4b09-a509-30044964574e",
	    "farmName": "farm1",
	    "balance": -1,
	    "isCreative": true,
	    "transactionsMade": 0,
	    "numberOfCycles": 0
    }

Where "id" "isCreative" and "farmName" are corresponded to a chosen farm. 
"balance" is total money that owner has (-1 in creative mode).
"transactionsMade" is a total number of transactions made with a current owner.
"numberOfCycles" is number of simulation cycles made with a given farm.

You may see your owner info with:

    GET: .../actions/owner-info

You may create RANDOM toads with:

    POST: .../actions/born-random-toad

You may create GROWN toads of any type you want (you MUST provide your toad info with a correct JSON http-entity) with:
    
    POST: .../actions/add-grown-toad

You may kill any toad (each toad will be killed in a random way) with:

    DELETE: .../actions/kill-toad?id=1234

You may see a list of all your toads with:

    GET: .../actions/get-all-toads


# TODO: finish doc
