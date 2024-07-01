package com.example.flexflow.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.FontWeight
import com.example.flexflow.rememberTextClassifier
import org.tensorflow.lite.support.label.Category
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
data class BigBlackDrawing(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val drawWidth: Dp = 5.dp
)


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SchedScreen(
    onBackClick: () -> Unit = {}
) {

    val textClassifier = rememberTextClassifier(modelPath = "mobilebert.tflite")
    var result by remember { mutableStateOf<String>("") }


    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState(), reverseScrolling = true)
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        //Top UI of the Create Journal Bar


        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "MonthBack",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )

            Text(
                "New Journal Event",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                fontSize = 24.sp
            )

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "MonthForward",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //UI For the options of the journal entry
        var titleText by rememberSaveable { mutableStateOf("") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement  =  Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "CheckTask",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )
            
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Title") }
            )

        }
        
        Spacer(modifier = Modifier.height(16.dp))

        var hrText by rememberSaveable { mutableStateOf("") }

        var minText by rememberSaveable { mutableStateOf("") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement  =  Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "CheckTask",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )

            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp)
                    .height(60.dp),
                value = hrText,
                onValueChange = { hrText = it },
                label = { Text("Hour") }
            )

            Text(
                " : ",
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(8.dp),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp)
                    .height(60.dp),
                value = minText,
                onValueChange = { minText = it },
                label = { Text("Min") }
            )





        }

        Spacer(modifier = Modifier.height(16.dp))


        //UI For the options of the journal entry
        var journalEntry by rememberSaveable { mutableStateOf("") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement  =  Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "CheckTask",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )

            OutlinedTextField(
                value = journalEntry,
                onValueChange = { journalEntry = it },
                label = { Text("Journal Entry") },
                modifier = Modifier.width(280.dp).height(150.dp)
            )

        }
        val client = OkHttpClient.Builder()
            .callTimeout(1, TimeUnit.MINUTES) // Total timeout for the entire operation
            .connectTimeout(30, TimeUnit.SECONDS) // Time allowed to establish initial connection
            .readTimeout(30, TimeUnit.SECONDS) // Time allowed for reading data from the server
            .writeTimeout(30, TimeUnit.SECONDS) // Time allowed for sending data to the server
            .build()
        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = journalEntry.toRequestBody(JSON)
        OutlinedButton(
            onClick = {
                GlobalScope.launch(Dispatchers.IO){
                    val request = Request.Builder()
                        .url("https://nt8szr9vcl.execute-api.us-east-1.amazonaws.com/dev/classify")
                        .post(body)
                        .build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        var answer = response.body.string()
                        result = answer
                    }
                }

            }
        ) {
                Text("Analyse")
            }

//        if(results.isNotEmpty()) {
//            Text(results[0].label + ": " + results[0].score)
//            Text(results[1].label + ": " + results[1].score)
//        }

        Text(result)
        Spacer(modifier = Modifier.height(16.dp))

        val drawings = remember { mutableStateListOf<BigBlackDrawing>() }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement  =  Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "CheckTask",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )

            Canvas(
                modifier = Modifier
                    .width(280.dp)
                    .height(150.dp)
                    .background(Color.LightGray)
                    .pointerInput(true) {

                        //This detects if a finger or stylus is in contact with the screen
                        detectDragGestures { change, dragAmount -> change.consume()

                            //Making a drawing based on the start and end position of the drag gesture
                            val drawing = BigBlackDrawing(
                                start = change.position - dragAmount,
                                end = change.position,
                                color = Color.Black,
                                drawWidth = 5.dp
                            )

                            //Saving the drawing in list of drawings
                            drawings.add(drawing)

                        }
                    }
            ) {
                //Drawing the saved drawings
                drawings.forEach { drawing -> drawLine (
                    color = Color.Black,
                    start = drawing.start,
                    end = drawing.end,
                    strokeWidth = drawing.drawWidth.toPx(),
                    cap = StrokeCap.Round
                )
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement  =  Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            var recChecked by remember { mutableStateOf(true) }
            Switch(
                modifier = Modifier.semantics { contentDescription = "toggleRecommendations" },
                checked = recChecked,
                onCheckedChange = { recChecked = it },
                colors  = SwitchDefaults.colors(
                    checkedThumbColor = Color.Gray,
                    checkedTrackColor = Color.Black,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor= Color.Black,
                )
            )

            Text(
                "Allow Recommendations",
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement  =  Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            var sentChecked by remember { mutableStateOf(true) }
            Switch(
                modifier = Modifier.semantics { contentDescription = "toggleSentAnalysis" },
                checked = sentChecked,
                onCheckedChange = { sentChecked = it },
                colors  = SwitchDefaults.colors(
                    checkedThumbColor = Color.Gray,
                    checkedTrackColor = Color.Black,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor= Color.Black,
                )
            )

            Text(
                "Allow Sentiment Analysis",
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(2.dp))





    }



}