# Steps to update lambda function in aws

docker image prune
docker build -t bert-lambda .
docker tag bert-lambda $aws_account_id.dkr.ecr.$aws_region.amazonaws.com/bert-lambda
docker push $aws_account_id.dkr.ecr.$aws_region.amazonaws.com/bert-lambda:latest
serverless deploy

#Steps to run locally
docker image prune
docker build -t bert-lambda .
docker run -p 8080:8080 bert-lambda

then call in separate terminal with:

curl --request POST \
--url http://localhost:8080/2015-03-31/functions/function/invocations \
--header 'Content-Type: application/json' \
--data <enter data here but not sure how to format it yet...>
