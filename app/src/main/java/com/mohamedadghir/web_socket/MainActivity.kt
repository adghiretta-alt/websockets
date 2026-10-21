
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

    // 1. Declaramos la variable a nivel de clase
    private lateinit var wsclient: MyWebSocketClient

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

    inner class MyWebSocketClient(serverUri: URI) : WebSocketClient(serverUri) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            runOnUiThread {
                findViewById<TextView>(R.id.textView).text = "Conectado al servidor ✅"
            }
        }

        override fun onMessage(message: String?) {
            runOnUiThread {
                val tv = findViewById<TextView>(R.id.textView)
                tv.append("\n$message")
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            runOnUiThread {
                findViewById<TextView>(R.id.textView).text = "Conexión cerrada: $reason"
            }
        }

        override fun onError(ex: Exception?) {
            ex?.printStackTrace()
            runOnUiThread {
                findViewById<TextView>(R.id.textView).text = "Error: ${ex?.message}"
            }
        }
    }

    fun conectaWS() {
        // CORRECCIÓN: IP limpia + puerto donde corre tu servidor Node.js
        val uri = URI("ws://10.0.2.2:8080")

        wsclient = MyWebSocketClient(uri)
        wsclient.connect()
    }

    fun enviaMensaje(texto: String) {
        if (::wsclient.isInitialized && wsclient.isOpen && texto.isNotBlank()) {
            wsclient.send(texto)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::wsclient.isInitialized) {
            wsclient.close()
        }
    }
}
