#!/bin/bash

echo "Creating DynamoDB tables..."

# Create products table
awslocal dynamodb create-table \
    --table-name products \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5

echo "DynamoDB tables created successfully!"

# List tables to verify
awslocal dynamodb list-tables