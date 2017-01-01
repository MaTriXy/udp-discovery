package com.afollestad.udpdiscovery;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Aidan Follestad (afollestad)
 */
class Base {

    private Handler handler;
    private Thread thread;
    private Gson gson;
    private DatagramSocket senderSocket;
    private DatagramSocket receiverSocket;

    Context context;
    ErrorListener senderErrorListener;
    ErrorListener receiverErrorListener;
    EntityListener entityListener;
    RequestListener requestListener;


    @UiThread Base(Context context) {
        this.context = context;
        this.handler = new Handler();
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(AdapterFactory.create())
                .create();
    }

    private void createSenderSocket() {
        if (senderSocket != null) {
            return;
        }
        try {
            senderSocket = new DatagramSocket();
            senderSocket.setBroadcast(true);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create the sender socket!", e);
        }
    }

    private void createReceiverSocket() {
        if (receiverSocket != null) {
            return;
        }
        try {
            receiverSocket = new DatagramSocket(Constants.PORT, InetAddress.getByName("0.0.0.0"));
            receiverSocket.setBroadcast(true);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create the sender socket!", e);
        }
    }

    void sendPacket(@NonNull Object object, @NonNull Class<?> cls) {
        InetAddress broadcastAdr;
        try {
            broadcastAdr = Util.getBroadcastAddress(context);
        } catch (IOException e) {
            Util.handleError(senderErrorListener, e, "Failed to get the broadcast address!");
            return;
        }
        sendPacket(broadcastAdr, object, cls);
    }

    void sendPacket(@NonNull InetAddress to, @NonNull Object object, @NonNull Class<?> cls) {
        createSenderSocket();

        String jsonData = gson.toJson(object, cls);
        byte[] sendData;
        try {
            sendData = jsonData.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Util.handleError(senderErrorListener, e, "Failed to encode string to UTF-8 data!");
            return;
        }
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, to, Constants.PORT);
        try {
            senderSocket.send(sendPacket);
        } catch (IOException e) {
            Util.handleError(senderErrorListener, e, "Failed to send a packet!");
        }
    }

    void startReceiver() {
        if (thread != null) {
            return;
        }
        createReceiverSocket();
        thread = new Thread(new Runnable() {
            @Override public void run() {
                while (receiverSocket != null) {
                    byte[] recvBuf = new byte[Constants.BUFFER_SIZE];
                    final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

                    try {
                        receiverSocket.receive(packet);
                    } catch (IOException e) {
                        if (receiverSocket == null || thread == null || handler == null) {
                            break;
                        }
                        Util.handleError(receiverErrorListener, e, "Failed to receive a packet!", handler);
                    }

                    final InetAddress fromAdr = packet.getAddress();
                    String data;
                    try {
                        data = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Util.handleError(receiverErrorListener, e, "Failed to convert data to a UTF-8 string!", handler);
                        return;
                    }

                    final String myIp = Util.getWifiIpAddress();
                    if (myIp != null && myIp.equals(fromAdr.getHostAddress())) {
                        // Ignore broadcasts from the same device
                        continue;
                    }

                    final Entity entity = Entity.create()
                            .name(fromAdr.getCanonicalHostName())
                            .address(fromAdr.getHostAddress())
                            .build();
                    final Request request;
                    try {
                        request = gson.fromJson(data, Request.class)
                                .toBuilder()
                                .entity(entity)
                                .build();
                    } catch (JsonSyntaxException e) {
                        Util.handleError(receiverErrorListener, e, "Received invalid JSON from " + fromAdr.getHostAddress(), handler);
                        continue;
                    }

                    handler.post(new Runnable() {
                        @Override public void run() {
                            if (request.type().equals(RequestType.DISCOVER) && requestListener != null) {
                                // Received a discovery request
                                if (requestListener.onRequest(entity)) {
                                    Request response = Request.create()
                                            .version(BuildConfig.VERSION_CODE)
                                            .type(RequestType.RESPOND)
                                            .build();
                                    sendPacket(fromAdr, response, Request.class);
                                }
                            } else if (request.type().equals(RequestType.RESPOND) && entityListener != null) {
                                // Discovered an entity
                                entityListener.onEntity(entity);
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    void destroyBase() {
        context = null;
        handler = null;
        if (senderSocket != null) {
            senderSocket.close();
            senderSocket = null;
        }
        if (receiverSocket != null) {
            receiverSocket.close();
            receiverSocket = null;
        }
        senderErrorListener = null;
        receiverErrorListener = null;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
