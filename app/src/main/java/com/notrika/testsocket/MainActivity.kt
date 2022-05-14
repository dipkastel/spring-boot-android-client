package com.notrika.testsocket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import tech.gusavila92.websocketclient.WebSocketClient
import java.lang.Exception
import java.net.URI
import java.net.URISyntaxException
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var  webSocketClient: WebSocketClient;
    lateinit var btn_send :Button
    lateinit var btn_connect :Button
    lateinit var btn_subscribe :Button
    lateinit var edt_input :EditText
    lateinit var txt_result: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_send = this.findViewById<Button>(R.id.btn_send);
        btn_connect = this.findViewById<Button>(R.id.btn_connect);
        btn_subscribe = this.findViewById<Button>(R.id.btn_subscribe);
         edt_input = this.findViewById<EditText>(R.id.edt_input);
         txt_result = this.findViewById<TextView>(R.id.txt_result);
        btn_send.setOnClickListener{
            var greeting = Greeting()
            greeting.foo = edt_input.text.toString()
            greeting.bar = edt_input.text.toString()
            greeting.baz = edt_input.text.toString()
            var toSend = "[\"SEND\ndestination:/app/hello/11\ncontent-length:34\n\n"+Gson().toJson(greeting)+"]"
            var test = "[\"SEND\\ndestination:/app/hello/11\\ncontent-length:34\\n\\n{\\\"foo\\\":\\\"hi\\\",\\\"bar\\\":\\\"hi\\\",\\\"baz\\\":\\\"hi\\\"}\\u0000\"]"
            webSocketClient.send(test)
        }
        btn_connect.setOnClickListener {
            webSocketClient.send("[\"CONNECT\\naccept-version:1.1,1.0\\nheart-beat:10000,10000\\n\\n\\u0000\"]");

        }
        btn_subscribe.setOnClickListener {
            webSocketClient.send("[\"SUBSCRIBE\\nid:sub-0\\ndestination:/chat/11/greetings\\n\\n\\u0000\"]");

        }
        createWebSocketClient();
    }


    private fun createWebSocketClient() {
        var secret = getRandomString(8)
        var code = Random.nextInt(100, 999)
        var uri:URI?
        try {
            // Connect to local host
            uri = URI("ws://192.168.0.117:8383/GympinChatEndPoint/"+code.toString()+"/"+secret+"/websocket");
        }
        catch ( e: URISyntaxException) {
            e.printStackTrace();
            return;
        }

        webSocketClient = object: WebSocketClient(uri) {
            override fun onOpen() {
                runOnUiThread {
                    txt_result.text="Session is starting"+"\n";
                }
//                webSocketClient.send("['CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\u0000']");
//                txt_result.text="CONNECT Sent"+"\n";
//                webSocketClient.send("['SUBSCRIBE\\nid:sub-0\\ndestination:/chat/11/greetings\\n\\n\\u0000']");
//                txt_result.text="SUBSCRIBE Sent"+"\n";
            }

            override fun onTextReceived(message: String?) {
                runOnUiThread {
                    txt_result.text = message + "\n" + txt_result.text;
                }
            }

            override fun onBinaryReceived(data: ByteArray?) {

                runOnUiThread {
                    txt_result.text = "onBinaryReceived" + "\n";
                }
            }

            override fun onPingReceived(data: ByteArray?) {

                runOnUiThread {
                    txt_result.text = "onPingReceived" + "\n";
                }
            }

            override fun onPongReceived(data: ByteArray?) {

                runOnUiThread {
                    txt_result.text = "onPongReceived" + "\n";
                }
            }

            override fun onException(e: Exception?) {
                Log.i("WebSocket", e?.message.toString());

                runOnUiThread {
                    txt_result.text = e?.message.toString() + "\n" + txt_result.text
                }
            }

            override fun onCloseReceived() {

                runOnUiThread {
                    txt_result.text = "Closed " + "\n" + txt_result.text
                }
            }
        };

         webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }
    fun getRandomString(length: Int) : String {
        val allowedChars =  ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}