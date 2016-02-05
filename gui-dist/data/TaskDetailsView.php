<?php

//This is just a dummy backend mocking a db-server architecture.

header('Content-Type: application/json');
$id = $_GET["id"];
$type = $_GET["type"];

if ($id == 4) {
	if ($type == "Actions") {
		echo '[
			{
				"id": 2,
				"task": 4,
				"name": "Sprint 19"
			},
			{
				"id": 3,
				"task": 4,
				"name": "Sprint 21"
			},
			{
				"id": 9,
				"task": 4,
				"name": "Sprint 4"
			}
		]';
	} else if ($type == "Comments") {
		echo '[
			{
				"id": 54,
				"task": 4,
				"testProp": "",
				"dateOfCreation": "1.1.2015",
				"newTask": ""
			}
		]';
	}
} else if ($id == 7) {
	if ($type == "Actions") {
		echo '[
			{
				"id": 42,
				"task": 7,
				"name": "Sprint 119"
			},
			{
				"id": 3463,
				"task": 7,
				"name": "Sprint 291"
			},
			{
				"id": 93,
				"task": 7,
				"name": "Sprint 43"
			}
		]';
	} else if ($type == "Comments") {
		echo '[
			{
				"id": 73,
				"task": 7,
				"testProp": "",
				"dateOfCreation": "5.2.2015",
				"newTask": ""
			}
		]';
	}
} else {
	http_response_code(404);
}
?>