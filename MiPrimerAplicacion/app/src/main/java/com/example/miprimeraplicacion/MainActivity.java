package com.example.miprimeraplicacion;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private TextView textViewChat;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        textViewChat = findViewById(R.id.textViewChat);
        Button buttonSend = findViewById(R.id.buttonSend);
        Button buttonExit = findViewById(R.id.buttonExit);

        // Iniciar el hilo para conectarse al servidor y recibir mensajes
        new Thread(() -> {
            try {
                // Cambia a la dirección IP de tu servidor
                socket = new Socket("172.17.44.11", 1717);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());

                while (true) {
                    // Escuchar continuamente los mensajes del servidor
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        runOnUiThread(() -> {
                            textViewChat.append("Servidor: " + message + "\n");
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        buttonSend.setOnClickListener(view -> {
            String message = editTextMessage.getText().toString();
            sendMessage(message); // Llama a la función para enviar el mensaje
            textViewChat.append("Yo: " + message + "\n");
            editTextMessage.setText("");
        });

        buttonExit.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExitActivity.class);
            startActivity(intent);
        });
    }

    private void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (out != null) {
                    out.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}