## Toads general logic

Any toad has:

"age" that is increases by 1 after each time cycle

"isMale" sex so only male and female toads can be bred together to create new tadpoles

"isCannibal" characteristic, so only cannibal toads can eat other toads

"pollutionLevel", if your toads are polluted, they will produce fewer tadpoles

"hungerLevel" Your toads should eat to survive, either food you give them or other toads.
If 'hungerLevel' increases too much, the toad will die. 
Cannibal toads will not eat other toads if they are fed.

### Each toad can be one of the following types:

#### 1. Tadpole

Tadpole is a baby toad that can transform into a grown toad after 100 time cycles.

Tadpoles CANNOT be bred until they become grown toads. Tadpoles can be eaten by cannibals, 
but they decrease hunger level less for cannibals. (TODO)

Tadpoles-cannibals CANNOT eat other toads until they become adult.

Tadpole has the following structure:

    {
		"id": "b38e0a80-cc30-4f0f-9bec-6b04b5e818f0",
		"farmName": "farm1",
		"name": "Jane",
		"breed": "Lepidobatrachus laevis",
		"isMale": false,
		"age": 0,
		"isCannibal": true,
		"rarity": {
			"value": "rare"
		},
		"color": "grey",
		"mutations": [
		],
		"pollutionLevel": {
			"level": 65
		},
		"hungerLevel": {
			"level": 0
		}
	}

Where "breed", "color", "rarity", "pollutionLevel" are personal characteristics that can be inherited from parents.

#### 2. Grown toad

Grown toad is an adult toad that can reproduce if "fertility" of a toad is true.

!TODO!
Grown toad has the following structure:

    {
		"id": "b38e0a80-cc30-4f0f-9bec-6b04b5e818f0",
		"farmName": "farm1",
		"name": "Jane",
		"breed": "Lepidobatrachus laevis",
		"isMale": false,
		"age": 0,
		"isCannibal": true,
		"rarity": {
			"value": "rare"
		},
		"color": "grey",
        "diseaseStatus": ""
		"mutations": [
		],
		"pollutionLevel": {
			"level": 65
		},
        "fertility": true,
		"hungerLevel": {
			"level": 0
		}
	}

#### 3. Pregnant toad TODO

#### 4. Dead toad TODO