package com.example.flexflow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier

class SentimentAnalyzer(
    private val bertClassifier: BertNLClassifier,
    private val coroutineScope: CoroutineScope
) : SentimentAnalysis {

    override fun detectSentiment(
        input: String,
        onResult: (List<Category>) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.Default) {
            val result = bertClassifier.classify(input)
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}