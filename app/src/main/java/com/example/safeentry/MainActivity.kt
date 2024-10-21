package com.example.safeentry

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var contadorPuertas = 0
    private lateinit var linearLayoutButtons: LinearLayout
    private lateinit var botonAñadirPuerta: Button
    private val maxPuertas = 10
    private val handler = Handler()
    private var eliminarRunnable: Runnable? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutButtons = findViewById(R.id.linear_layout_buttons)
        botonAñadirPuerta = findViewById(R.id.boton_añadir_puerta)
        botonAñadirPuerta.setOnClickListener {
            if (contadorPuertas < maxPuertas) {
                solicitarNombrePuerta()
            }
            if (contadorPuertas >= maxPuertas) {
                botonAñadirPuerta.visibility = Button.GONE
                Toast.makeText(this, "Se alcanzó el límite de 10 puertas", Toast.LENGTH_SHORT).show()
            }
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.barra_navegacion)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    Toast.makeText(this, "Inicio seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_registro -> {
                    Toast.makeText(this, "Registro seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_ayuda -> {
                    Toast.makeText(this, "Ayuda seleccionada", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_configuracion -> {
                    Toast.makeText(this, "Configuración seleccionada", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_perfil -> {
                    Toast.makeText(this, "Perfil seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
    private fun solicitarNombrePuerta() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Asignar nombre a la puerta")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val nombrePuerta = input.text.toString()
            añadirPuerta(nombrePuerta)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            añadirPuerta("Puerta ${contadorPuertas + 1}")
            dialog.dismiss()
        }
        builder.show()
    }
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun añadirPuerta(nombrePuerta: String) {
        contadorPuertas += 1
        val layoutPuerta = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
        }
        val textViewNombrePuerta = TextView(this).apply {
            text = nombrePuerta
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.5f
            ).apply {
                gravity = android.view.Gravity.CENTER
                setMargins(20, 0, 10, 0)
            }
            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        eliminarRunnable = Runnable {
                            confirmarAccion(
                                "¿Seguro que deseas eliminar la $nombrePuerta?",
                                onConfirm = {
                                    eliminarPuerta(layoutPuerta)
                                }
                            )
                        }
                        handler.postDelayed(eliminarRunnable!!, 1000)
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        handler.removeCallbacks(eliminarRunnable!!)
                        true
                    }
                    else -> false
                }
            }
        }
        val buttonPuerta = Button(this).apply {
            text = "Activado"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f).apply {
                setMargins(10, 0, 20, 0)
            }
            background = resources.getDrawable(R.drawable.boton_activado, theme)
            setBackgroundResource(R.drawable.boton_activado)
            setOnClickListener {
                confirmarAccion(
                    "¿Seguro que deseas ${if (text == "Activado") "desactivar" else "activar"} la $nombrePuerta?",
                    onConfirm = {
                        if (text == "Activado") {
                            text = "Desactivado"
                            setBackgroundResource(R.drawable.boton_desactivado)
                        } else {
                            text = "Activado"
                            setBackgroundResource(R.drawable.boton_activado)
                        }
                    }
                )
            }
        }


        val imageViewEdit = ImageView(this).apply {
            setImageResource(R.drawable.ic_lapiz)
            layoutParams = LinearLayout.LayoutParams(
                80,
                80
            ).apply {
                setMargins(10, 0, 10, 0)
                gravity = Gravity.CENTER
            }
            setOnClickListener {
                cambiarNombrePuerta(textViewNombrePuerta)
            }
        }
        layoutPuerta.addView(textViewNombrePuerta)
        layoutPuerta.addView(imageViewEdit)
        layoutPuerta.addView(buttonPuerta)
        linearLayoutButtons.addView(layoutPuerta)
        Toast.makeText(this, "$nombrePuerta añadida", Toast.LENGTH_SHORT).show()
    }
    private fun cambiarNombrePuerta(textView: TextView) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar nombre de la puerta")
        val input = EditText(this)
        input.setText(textView.text)
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val nuevoNombre = input.text.toString()
            textView.text = nuevoNombre
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
    private fun confirmarAccion(mensaje: String, onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mensaje)
            .setPositiveButton("Sí") { _, _ -> onConfirm() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    private fun eliminarPuerta(layoutPuerta: LinearLayout) {
        linearLayoutButtons.removeView(layoutPuerta)
        contadorPuertas -= 1
        Toast.makeText(this, "Puerta eliminada", Toast.LENGTH_SHORT).show()
        if (contadorPuertas < maxPuertas) {
            botonAñadirPuerta.visibility = Button.VISIBLE
        }
    }
}
