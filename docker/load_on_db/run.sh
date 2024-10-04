#!/bin/sh
echo "==========> sleeping 30 seconds for mysql to initialize"
sleep 30

jq -r '.starships[] | [.name, .class, .captain, .launched_year] | @csv' starships.json > starships.csv

echo "==========> Importing starships data:"

cat starships.csv

mysql --local-infile=1 -h mysql -P 3306 -u root --password='password' -D starfleet_db -e "SET GLOBAL local_infile=1; USE starfleet_db; load data local infile 'starships.csv' into table starships FIELDS TERMINATED BY ',' (name, class, captain, launched_year)"