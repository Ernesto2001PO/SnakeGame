package com.example.snake.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.example.snake.Activity.MainActivity.Companion.STEP_SIZE
import android.view.MotionEvent
import android.widget.Toast
import kotlin.random.Random

class SnakeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paintSnake = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 10f
    }

    private val paintApple = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 10f
    }

    // Posición inicial de la serpiente
    private var snakeX = Random.nextInt(0, 1000).toFloat()
    private var snakeY = Random.nextInt(0, 1000).toFloat()


    // Posición inicial de la manzana
    private var appleX = Random.nextInt(0, 1000).toFloat()
    private var appleY = Random.nextInt(0, 1000).toFloat()

    private var directionX = 0f
    private var directionY = 0f
    private var isGameOver = false
    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            moveSnake(directionX, directionY)
            handler.postDelayed(this, 50) // Continuar moviéndose
        }

    }

    init {
        handler.post(updateRunnable) // Iniciar el bucle de movimiento
    }

    private var snakeSegments =
        mutableListOf<Pair<Float, Float>>() // Lista de segmentos de la serpiente

    init {
        snakeSegments.add(Pair(snakeX, snakeY)) // Añadir la cabeza de la serpiente
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar todos los segmentos de la serpiente
        for (segment in snakeSegments) {
            canvas.drawRect(
                segment.first,
                segment.second,
                segment.first + 80f,
                segment.second + 100f,
                paintSnake
            )
        }

        // Dibujar la manzana
        canvas.drawCircle(
            appleX,
            appleY,
            50f,
            paintApple
        )

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            val widthThird = width / 3.0f
            val heightHalf = height / 2.0f

            if (y < heightHalf && x > widthThird && x < 2 * widthThird && directionY == 0f) {
                directionY = -STEP_SIZE.toFloat() // Arriba
                directionX = 0f
            } else if (y > heightHalf && x > widthThird && x < 2 * widthThird && directionY == 0f) {
                directionY = STEP_SIZE.toFloat() // Abajo
                directionX = 0f
            } else if (x < widthThird && directionX == 0f) {
                directionX = -STEP_SIZE.toFloat() // Izquierda
                directionY = 0f
            } else if (x > 2 * widthThird && directionX == 0f) {
                directionX = STEP_SIZE.toFloat() // Derecha
                directionY = 0f
            }
            return true
        }
        return super.onTouchEvent(event)
    }


    fun moveSnake(dx: Float, dy: Float) {
        // Mover la serpiente
        snakeX += dx
        snakeY += dy

        // Verificar si la serpiente se toca a sí misma
        if (checkSelfCollision()) {
            // Mostrar mensaje y detener el juego
            showGameOverMessage()
            return  // Detener la ejecución del juego
        }
        
        // Añadir el nuevo segmento (la cabeza) al principio de la lista
        snakeSegments.add(0, Pair(snakeX, snakeY))

        // Verificar colisión con la manzana
        if (checkAppleCollision()) {
            // Si se come la manzana, generar nueva posición para la manzana
            generateNewApplePosition()
        } else {
            // Si no se ha comido la manzana, remover el último segmento para que la serpiente no crezca
            snakeSegments.removeAt(snakeSegments.size - 1)
        }

        // Verificar si la serpiente se ha salido de los límites y teletransportarla al otro lado
        checkBoundaryCrossing()

        // Redibujar la vista con la nueva posición
        invalidate()
    }



    private fun showGameOverMessage() {
        // Mostrar mensaje de Game Over
        if (!isGameOver) {
            isGameOver = true
            Toast.makeText(context, "Game Over", Toast.LENGTH_SHORT).show()
        }
    }


    private fun generateNewApplePosition() {
        appleX = Random.nextInt(0, width).toFloat()
        appleY = Random.nextInt(0, height).toFloat()
    }

    private fun checkAppleCollision(): Boolean {
        // Verificar si la cabeza de la serpiente toca la manzana
        val distanceX = snakeX - appleX
        val distanceY = snakeY - appleY
        val distance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble())

        return distance < 100 // Ajusta este valor según el tamaño de la serpiente y la manzana
    }

    private fun checkBoundaryCrossing() {
        // Teletransportar la serpiente si se sale de los límites de la pantalla
        if (snakeX < 0)
            snakeX = width.toFloat()
        if (snakeX > width)
            snakeX = 0f
        if (snakeY < 0)
            snakeY = height.toFloat()
        if (snakeY > height)
            snakeY = 0f
    }

    private fun checkSelfCollision(): Boolean {
        // Obtener la cabeza de la serpiente (primer segmento)
        val head = snakeSegments[0]

        // Iterar sobre el resto de los segmentos del cuerpo (omitimos el primero que es la cabeza)
        for (i in 1 until snakeSegments.size) {
            val segment = snakeSegments[i]

            // Verificar si la posición de la cabeza coincide con la de algún segmento del cuerpo
            if (head.first == segment.first && head.second == segment.second) {
                return true  // Colisión detectada
            }
        }
        return false  // No hay colisión
    }


}



