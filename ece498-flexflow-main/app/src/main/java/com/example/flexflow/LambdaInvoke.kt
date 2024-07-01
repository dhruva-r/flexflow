package com.example.flexflow

import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.CreateFunctionRequest
import aws.sdk.kotlin.services.lambda.model.FunctionCode
import aws.sdk.kotlin.services.lambda.model.InvokeRequest
import aws.sdk.kotlin.services.lambda.model.LogType
import aws.sdk.kotlin.services.lambda.model.Runtime
import aws.sdk.kotlin.services.lambda.waiters.waitUntilFunctionActive
import kotlin.system.exitProcess

/**
 * This is how we are going to call the lambdas in AWS
 */
class LambdaInvoke {

    suspend fun invokeFunction(functionNameVal: String, inp : String) {

        val json = inp
        val byteArray = json.trimIndent().encodeToByteArray()
        val request = InvokeRequest {
            functionName = functionNameVal
            logType = LogType.Tail
            payload = byteArray
        }

        LambdaClient { region = "ca-central-1" }.use { awsLambda ->
            val res = awsLambda.invoke(request)
            println("${res.payload?.toString(Charsets.UTF_8)}")
            println("The log result is ${res.logResult}")
        }
    }

}