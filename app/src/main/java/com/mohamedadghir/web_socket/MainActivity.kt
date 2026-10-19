
package com.mohamedadghir.web_socket

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        conectaWS()
    }

    // "inner" es importante para acceder a los métodos de MainActivity
    inner class MyWebSocketClient(serverUri: URI) : WebSocketClient(serverUri) {

        override fun onOpen(handshakedata: ServerHandshake?) {
            println("Conectado al servidor")
            println("Código de estado: ${handshakedata?.httpStatus}")
            println("Descripción: ${handshakedata?.httpStatusMessage}")

            runOnUiThread {
                val tv = findViewById<TextView>(R.id.textView)
                tv.text = "Conectado al servidor"
            }
        }

        override fun onMessage(message: String?) {
            println("📥 Recibo: $message")

            runOnUiThread {
                val tv = findViewById<TextView>(R.id.textView)
                tv.text = "Nuevo mensaje: $message"
            }
        }

        override fun onClose(
            code: Int,
            reason: String?,
            remote: Boolean
        ) {
            println("Conexión cerrada")
            println("Código: $code, Motivo: $reason, Remote: $remote")

            runOnUiThread {
                val tv = findViewById<TextView>(R.id.textView)
                tv.text = "Conexión cerrada: $reason"
            }
        }

        override fun onError(ex: Exception?) {
            println("Error: ${ex?.message}")
            ex?.printStackTrace()

            runOnUiThread {
                val tv = findViewById<TextView>(R.id.textView)
                tv.text = "Error de conexión: ${ex?.message}"
            }
        }
    }

    fun conectaWS() {
        val uri = URI("ws://10.0.2.2.")

        // Utiliza wss:// si la conexión es segura
        val wsclient = MyWebSocketClient(uri)

        wsclient.connect()
    }
}
