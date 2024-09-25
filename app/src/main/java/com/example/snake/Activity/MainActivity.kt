package com.example.snake.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.snake.R
import com.example.snake.View.SnakeView


class MainActivity : AppCompatActivity() {

    companion object {
        const val STEP_SIZE = 30 // Cada paso mueve la serpiente 30 píxeles.
    }

    private lateinit var snakeView: SnakeView  // Vista personalizada que dibuja la serpiente.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snakeView = findViewById(R.id.snakeView)



    }

}
