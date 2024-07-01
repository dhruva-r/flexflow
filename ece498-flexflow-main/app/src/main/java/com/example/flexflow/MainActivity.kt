package com.example.flexflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.flexflow.FlexFlowApp
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale



@AndroidEntryPoint
class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlexFlowApp()
        }

    }

}

