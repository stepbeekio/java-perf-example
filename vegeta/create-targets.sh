#!/usr/bin/env bash

BASEPATH=$(pwd)

rm targets.txt

echo "GET http://localhost:8080/todo" >> targets.txt
echo "" >> targets.txt
echo "POST http://localhost:8080/todo" >> targets.txt
echo "Content-Type: application/json" >> targets.txt
echo "@$BASEPATH/create-todo.json" >> targets.txt
echo "" >> targets.txt
echo "POST http://localhost:8080/todo/random" >> targets.txt
echo "Content-Type: application/json" >> targets.txt
echo "@$BASEPATH/create-todo.json" >> targets.txt
echo "" >> targets.txt
echo "PUT http://localhost:8080/todo/1" >> targets.txt
echo "Content-Type: application/json" >> targets.txt
echo "@$BASEPATH/edit-todo.json" >> targets.txt
echo "" >> targets.txt
echo "GET http://localhost:8080/todo?size=50" >> targets.txt
echo "" >> targets.txt
echo "GET http://localhost:8080/todo?size=500" >> targets.txt
