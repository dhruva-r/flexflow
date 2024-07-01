package com.example.flexflow

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier

interface SentimentAnalysis {
    fun detectSentiment(input: String, onResult: (List<Category>) -> Unit)
}

@Composable
fun rememberTextClassifier(
    delegate: Int = 0,
    modelPath: String,
): SentimentAnalysis {
    val localContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    return remember {
        val baseOptionsBuilder = BaseOptions.builder()
        when (delegate) {
            1 -> {
                baseOptionsBuilder.useNnapi()
            }
        }
        val baseOptions = baseOptionsBuilder.build()
        setUpClassifier(localContext, coroutineScope, baseOptions, modelPath)
    }
}

private fun setUpClassifier(
    context: Context,
    coroutineScope: CoroutineScope,
    baseOptions: BaseOptions,
    modelPath: String
): SentimentAnalyzer {

    val options = BertNLClassifier.BertNLClassifierOptions
        .builder()
        .setBaseOptions(baseOptions)
        .build()


    return SentimentAnalyzer(
        BertNLClassifier.createFromFileAndOptions(context, modelPath, options),
        coroutineScope
    )
}
